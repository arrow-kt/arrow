package arrow.effects.internal

import arrow.core.Either
import arrow.core.nonFatalOrThrow
import arrow.effects.CancelToken
import arrow.effects.ForIO
import arrow.effects.IO
import arrow.effects.IOConnection
import arrow.effects.IOOf
import arrow.effects.fix
import arrow.effects.typeclasses.ExitCase
import java.util.concurrent.atomic.AtomicBoolean

@PublishedApi
internal class BracketStart<A, B>(
  val use: (A) -> IOOf<B>,
  val release: (A, ExitCase<Throwable>) -> IOOf<Unit>,
  val conn: IOConnection,
  val forwardCancel: ForwardCancelable,
  val cb: (Either<Throwable, B>) -> Unit
) : (Either<Throwable, A>) -> Unit {

  override fun invoke(ea: Either<Throwable, A>) {
    Platform.trampoline {
      when (ea) {
        is Either.Right -> {
          val a = ea.b
          val frame = BracketReleaseFrame<A, B>(a, release)

          val fb: IOOf<B> = try {
            use(a)
          } catch (e: Throwable) {
            IO.raiseError(e.nonFatalOrThrow())
          }

          // Registering our cancelable token ensures that in case cancellation is detected, release gets called
          forwardCancel.complete(frame.cancel)
          // Actual execution
          IORunLoop.startCancelable(IO.FlatMap(fb, frame), conn, cb = cb)
        }
        is Either.Left -> cb(ea)
      }
    }
  }
}

private class ReleaseRecover(val error: Throwable) : IOFrame<Unit, IO<Nothing>> {
  override fun recover(e: Throwable): IO<Nothing> = IO.raiseError(Platform.composeErrors(error, e))
  override fun invoke(a: Unit): IO<Nothing> = IO.raiseError(error)
}

internal abstract class BaseReleaseFrame<A, B> : IOFrame<B, IO<B>> {

  // Guard used for thread-safety, to ensure the idempotency  of the release; otherwise `release` can be called twice
  private val waitsForResult = AtomicBoolean(true)

  abstract fun release(c: ExitCase<Throwable>): CancelToken<ForIO>

  private fun applyRelease(e: ExitCase<Throwable>): IO<Unit> = IO.defer {
    if (waitsForResult.compareAndSet(true, false)) release(e)
    else IO.unit
  }

  val cancel: CancelToken<ForIO> = applyRelease(ExitCase.Canceled).fix().uncancelable()

  // Unregistering cancel token, otherwise we can have a memory leak; N.B. conn.pop() happens after the evaluation of `release`, because
  // otherwise we might have a conflict with the auto-cancellation logic
  override fun recover(e: Throwable): IO<B> = IO.FlatMap(
    IO.ConnectionSwitch(applyRelease(ExitCase.Error(e)), IO.ConnectionSwitch.makeUncancelable, IO.ConnectionSwitch.disableUncancelableAndPop),
    ReleaseRecover(e))

  override operator fun invoke(a: B): IO<B> = IO.Map(
    IO.ConnectionSwitch(applyRelease(ExitCase.Completed), IO.ConnectionSwitch.makeUncancelable, IO.ConnectionSwitch.disableUncancelableAndPop)
  ) { a }
}

internal class BracketReleaseFrame<A, B>(val a: A, val releaseFn: (A, ExitCase<Throwable>) -> IOOf<Unit>) : BaseReleaseFrame<A, B>() {
  override fun release(c: ExitCase<Throwable>): CancelToken<ForIO> = releaseFn(a, c)
}

internal class GuaranteeReleaseFrame<A>(val releaseFn: (ExitCase<Throwable>) -> IOOf<Unit>) : BaseReleaseFrame<Unit, A>() {
  override fun release(c: ExitCase<Throwable>): CancelToken<ForIO> = releaseFn(c)
}
