package arrow.effects.suspended.fx

import arrow.core.Either
import arrow.core.nonFatalOrThrow
import arrow.effects.CancelToken
import arrow.effects.internal.Platform
import arrow.effects.typeclasses.Bracket
import arrow.effects.typeclasses.Concurrent
import arrow.effects.typeclasses.ExitCase
import java.util.concurrent.atomic.AtomicBoolean

@PublishedApi
internal class BracketStart<A, B>(
  val use: (A) -> FxOf<B>,
  val release: (A, ExitCase<Throwable>) -> FxOf<Unit>,
  val conn: FxConnection,
  val forwardCancel: FxForwardCancelable,
  val cb: (Either<Throwable, B>) -> Unit
) : (Either<Throwable, A>) -> Unit {

  override fun invoke(ea: Either<Throwable, A>) {
    Platform.trampoline {
      when (ea) {
        is Either.Right -> {
          val a = ea.b
          val frame = BracketReleaseFrame<A, B>(a, release)

          val fb: FxOf<B> = try {
            use(a)
          } catch (e: Throwable) {
            Fx.raiseError(e.nonFatalOrThrow())
          }

          // Registering our cancelable token ensures that in case cancellation is detected, release gets called
          forwardCancel.complete(frame.cancel)
          // Actual execution
          FxRunLoop.startCancelable(Fx.FlatMap(fb, frame), conn, cb = cb)
        }
        is Either.Left -> cb(ea)
      }
    }
  }
}

private class ReleaseRecover(val error: Throwable) : FxFrame<Unit, Fx<Nothing>> {
  override fun recover(e: Throwable): Fx<Nothing> = Fx.raiseError(Platform.composeErrors(error, e))
  override fun invoke(a: Unit): Fx<Nothing> = Fx.raiseError(error)
}

internal abstract class BaseReleaseFrame<A, B> : FxFrame<B, Fx<B>> {

  // Guard used for thread-safety, to ensure the idempotency  of the release; otherwise `release` can be called twice
  private val waitsForResult = AtomicBoolean(true)

  abstract fun release(c: ExitCase<Throwable>): CancelToken<ForFx>

  private fun applyRelease(e: ExitCase<Throwable>): Fx<Unit> = Fx.defer {
    if (waitsForResult.compareAndSet(true, false)) release(e)
    else Fx.unit
  }

  val cancel: CancelToken<ForFx> = applyRelease(ExitCase.Canceled).fix().uncancelable()

  // Unregistering cancel token, otherwise we can have a memory leak; N.B. conn.pop() happens after the evaluation of `release`, because
  // otherwise we might have a conflict with the auto-cancellation logic
  override fun recover(e: Throwable): Fx<B> = Fx.FlatMap(
    Fx.ConnectionSwitch(applyRelease(ExitCase.Error(e)), Fx.ConnectionSwitch.makeUncancelable, Fx.ConnectionSwitch.disableUncancelableAndPop),
    ReleaseRecover(e))

  override operator fun invoke(a: B): Fx<B> = Fx.Map(
    Fx.ConnectionSwitch(applyRelease(ExitCase.Completed), Fx.ConnectionSwitch.makeUncancelable, Fx.ConnectionSwitch.disableUncancelableAndPop)
  ) { a }
}

internal class BracketReleaseFrame<A, B>(val a: A, val releaseFn: (A, ExitCase<Throwable>) -> FxOf<Unit>) : BaseReleaseFrame<A, B>() {
  override fun release(c: ExitCase<Throwable>): CancelToken<ForFx> = releaseFn(a, c)
}

internal class GuaranteeReleaseFrame<A>(val releaseFn: (ExitCase<Throwable>) -> FxOf<Unit>) : BaseReleaseFrame<Unit, A>() {
  override fun release(c: ExitCase<Throwable>): CancelToken<ForFx> = releaseFn(c)
}
