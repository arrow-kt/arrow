package arrow.fx.internal

import arrow.core.Either
import arrow.core.nonFatalOrThrow
import arrow.fx.CancelToken
import arrow.fx.IO
import arrow.fx.IOConnection
import arrow.fx.IOFrame
import arrow.fx.IOOf
import arrow.fx.IOPartialOf
import arrow.fx.IORunLoop
import arrow.fx.fix
import arrow.fx.flatMap
import arrow.fx.typeclasses.ExitCase
import java.util.concurrent.atomic.AtomicBoolean

internal object IOBracket {

  /**
   * Implementation for `IO.bracketCase`.
   */
  operator fun <E, A, B> invoke(acquire: IOOf<E, A>, release: (A, ExitCase<E>) -> IOOf<E, Unit>, use: (A) -> IOOf<E, B>): IO<E, B> =
    IO.Async { conn, cb ->
      // Placeholder for the future finalizer
      val deferredRelease = ForwardCancelable<E>()
      conn.push(deferredRelease.cancel())

      // Race-condition check, avoiding starting the bracket if the connection
      // was cancelled already, to ensure that `cancel` really blocks if we
      // start `acquire` — n.b. `isCanceled` is visible here due to `push`
      if (!conn.isCanceled()) {
        // Note `acquire` is uncancelable due to usage of `IORunLoop.start`
        // (in other words it is disconnected from our IOConnection)
        IORunLoop.start(acquire, BracketStart(use, release, conn, deferredRelease, cb))
      } else {
        deferredRelease.complete(IO.unit)
      }
    }

  // Internals of `IO.bracketCase`.
  private class BracketStart<E, A, B>(
    val use: (A) -> IOOf<E, B>,
    val release: (A, ExitCase<E>) -> IOOf<E, Unit>,
    val conn: IOConnection,
    val deferredRelease: ForwardCancelable<E>,
    val cb: (Either<E, B>) -> Unit
  ) : (Either<E, A>) -> Unit {

    // This runnable is a dirty optimization to avoid some memory allocations;
    // This class switches from being a Callback to a Runnable, but relies on the internal IO callback protocol to be
    // respected (called at most once).
    private var result: Either<E, A>? = null

    override fun invoke(ea: Either<E, A>) {
      // Introducing a light async boundary, otherwise executing the required
      // logic directly will yield a StackOverflowException
      Platform.trampoline {
        when (ea) {
          is Either.Right -> {
            val a = ea.b
            val frame = BracketReleaseFrame<E, A, B>(a, release)
            val onNext = {
              val fb = try {
                use(a)
              } catch (e: Throwable) {
                IO.raiseError<B>(e.nonFatalOrThrow())
              }

              IO.Bind(fb.fix(), frame)
            }

            // Registering our cancelable token ensures that in case cancellation is detected, release gets called
            deferredRelease.complete(frame.cancel)
            // Actual execution
            IORunLoop.startCancelable(onNext(), conn, cb)
          }
          is Either.Left -> cb(ea)
        }
      }
    }
  }

  fun <E, A> guaranteeCase(source: IO<E, A>, release: (ExitCase<E>) -> IOOf<E, Unit>): IO<E, A> =
    IO.Async { conn, cb ->
      Platform.trampoline {
        val frame = EnsureReleaseFrame<E, A>(release)
        val onNext = source.flatMap(frame)
        // Registering our cancelable token ensures that in case
        // cancellation is detected, `release` gets called
        conn.push(frame.cancel)

        // Race condition check, avoiding starting `source` in case
        // the connection was already cancelled — n.b. we don't need
        // to trigger `release` otherwise, because it already happened
        if (!conn.isCanceled()) IORunLoop.startCancelable(onNext, conn, cb)
      }
    }

  private class BracketReleaseFrame<E, A, B>(val a: A, val releaseFn: (A, ExitCase<E>) -> IOOf<E, Unit>) :
    BaseReleaseFrame<E, A, B>() {

    override fun release(c: ExitCase<E>): CancelToken<IOPartialOf<E>> =
      releaseFn(a, c)
  }

  private class EnsureReleaseFrame<E, A>(val releaseFn: (ExitCase<E>) -> IOOf<E, Unit>) : BaseReleaseFrame<E, Unit, A>() {

    override fun release(c: ExitCase<E>): CancelToken<IOPartialOf<E>> = releaseFn(c)
  }

  private abstract class BaseReleaseFrame<E, A, B> : IOFrame<E, B, IO<E, B>> {

    // Guard used for thread-safety, to ensure the idempotency
    // of the release; otherwise `release` can be called twice
    private val waitsForResult = AtomicBoolean(true)

    abstract fun release(c: ExitCase<E>): CancelToken<IOPartialOf<E>>

    private fun applyRelease(e: ExitCase<E>): IO<E, Unit> =
      IO.defer(IO.rethrow) {
        if (waitsForResult.compareAndSet(true, false))
          release(e)
        else
          IO.unit
      }

    val cancel: CancelToken<IOPartialOf<E>> = applyRelease(ExitCase.Canceled).fix().uncancelable()

    // Unregistering cancel token, otherwise we can have a memory leak;
    // N.B. conn.pop() happens after the evaluation of `release`, because
    // otherwise we might have a conflict with the auto-cancellation logic
    override fun recover(e: E): IO<E, B> = IO.ContextSwitch(applyRelease(ExitCase.Error(e)), IO.ContextSwitch.makeUncancelable, disableUncancelableAndPop)
      .flatMap(ReleaseRecover(e))

    override operator fun invoke(a: B): IO<E, B> =
    // Unregistering cancel token, otherwise we can have a memory leak
    // N.B. conn.pop() happens after the evaluation of `release`, because
    // otherwise we might have a conflict with the auto-cancellation logic
      IO.ContextSwitch(applyRelease(ExitCase.Completed), IO.ContextSwitch.makeUncancelable, disableUncancelableAndPop)
        .map { a }
  }

  private class ReleaseRecover<E>(val error: E) : IOFrame<E, Unit, IO<E, Nothing>> {

    override fun recover(e: E): IO<E, Nothing> =
      IO.raiseError(Platform.composeErrors(error, e))

    override fun invoke(a: Unit): IO<E, Nothing> = IO.raiseError(error)
  }

  private val disableUncancelableAndPop: (Any?, Any?, IOConnection, IOConnection) -> IOConnection =
    { _, _, old, _ ->
      old.pop()
      old
    }
}
