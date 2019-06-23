package arrow.fx.internal

import arrow.core.Either
import arrow.core.nonFatalOrThrow
import arrow.fx.CancelToken
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.IOConnection
import arrow.fx.IOFrame
import arrow.fx.IOOf
import arrow.fx.IORunLoop
import arrow.fx.fix
import arrow.fx.typeclasses.ExitCase
import java.util.concurrent.atomic.AtomicBoolean

internal object IOBracket {

  /**
   * Implementation for `IO.bracketCase`.
   */
  operator fun <A, B> invoke(acquire: IOOf<A>, release: (A, ExitCase<Throwable>) -> IOOf<Unit>, use: (A) -> IOOf<B>): IO<B> =
    IO.Async { conn, cb ->
      // Placeholder for the future finalizer
      val deferredRelease = ForwardCancelable()
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
  private class BracketStart<A, B>(
    val use: (A) -> IOOf<B>,
    val release: (A, ExitCase<Throwable>) -> IOOf<Unit>,
    val conn: IOConnection,
    val deferredRelease: ForwardCancelable,
    val cb: (Either<Throwable, B>) -> Unit
  ) : (Either<Throwable, A>) -> Unit {

    // This runnable is a dirty optimization to avoid some memory allocations;
    // This class switches from being a Callback to a Runnable, but relies on the internal IO callback protocol to be
    // respected (called at most once).
    private var result: Either<Throwable, A>? = null

    override fun invoke(ea: Either<Throwable, A>) {
      // Introducing a light async boundary, otherwise executing the required
      // logic directly will yield a StackOverflowException
      Platform.trampoline {
        when (ea) {
          is Either.Right -> {
            val a = ea.b
            val frame = BracketReleaseFrame<A, B>(a, release)
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

  fun <A> guaranteeCase(source: IO<A>, release: (ExitCase<Throwable>) -> IOOf<Unit>): IO<A> =
    IO.Async { conn, cb ->
      Platform.trampoline {
        val frame = EnsureReleaseFrame<A>(release)
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

  private class BracketReleaseFrame<A, B>(val a: A, val releaseFn: (A, ExitCase<Throwable>) -> IOOf<Unit>) :
    BaseReleaseFrame<A, B>() {

    override fun release(c: ExitCase<Throwable>): CancelToken<ForIO> =
      releaseFn(a, c)
  }

  private class EnsureReleaseFrame<A>(val releaseFn: (ExitCase<Throwable>) -> IOOf<Unit>) : BaseReleaseFrame<Unit, A>() {

    override fun release(c: ExitCase<Throwable>): CancelToken<ForIO> = releaseFn(c)
  }

  private abstract class BaseReleaseFrame<A, B> : IOFrame<B, IO<B>> {

    // Guard used for thread-safety, to ensure the idempotency
    // of the release; otherwise `release` can be called twice
    private val waitsForResult = AtomicBoolean(true)

    abstract fun release(c: ExitCase<Throwable>): CancelToken<ForIO>

    private fun applyRelease(e: ExitCase<Throwable>): IO<Unit> =
      IO.defer {
        if (waitsForResult.compareAndSet(true, false))
          release(e)
        else
          IO.unit
      }

    val cancel: CancelToken<ForIO> = applyRelease(ExitCase.Canceled).fix().uncancelable()

    // Unregistering cancel token, otherwise we can have a memory leak;
    // N.B. conn.pop() happens after the evaluation of `release`, because
    // otherwise we might have a conflict with the auto-cancellation logic
    override fun recover(e: Throwable): IO<B> = IO.ContextSwitch(applyRelease(ExitCase.Error(e)), IO.ContextSwitch.makeUncancelable, disableUncancelableAndPop)
      .flatMap(ReleaseRecover(e))

    override operator fun invoke(a: B): IO<B> =
    // Unregistering cancel token, otherwise we can have a memory leak
    // N.B. conn.pop() happens after the evaluation of `release`, because
    // otherwise we might have a conflict with the auto-cancellation logic
      IO.ContextSwitch(applyRelease(ExitCase.Completed), IO.ContextSwitch.makeUncancelable, disableUncancelableAndPop)
        .map { a }
  }

  private class ReleaseRecover(val error: Throwable) : IOFrame<Unit, IO<Nothing>> {

    override fun recover(e: Throwable): IO<Nothing> =
      IO.raiseError(Platform.composeErrors(error, e))

    override fun invoke(a: Unit): IO<Nothing> = IO.raiseError(error)
  }

  private val disableUncancelableAndPop: (Any?, Throwable?, IOConnection, IOConnection) -> IOConnection =
    { _, _, old, _ ->
      old.pop()
      old
    }
}
