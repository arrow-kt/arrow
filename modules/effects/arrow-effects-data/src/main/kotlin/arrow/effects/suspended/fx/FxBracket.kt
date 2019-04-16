package arrow.effects.suspended.fx

import arrow.core.Either
import arrow.core.NonFatal
import arrow.effects.*
import arrow.effects.internal.Platform
import arrow.effects.typeclasses.ExitCase
import java.util.concurrent.atomic.AtomicBoolean

/**
 * DEV DOCS
 *
 * [FxBracket] exists out of a couple stages:
 *   1. acquisition
 *   2. consumption
 *   3. releasing
 *
 * 1. Resource acquisition is **NON CANCELABLE**, and this is the trickiest to implement and test
 *    because we cannot incorporate it into [BracketLaws] since cancellation is introduced in [Concurrent].
 *    If resource acquisition fails, meaning no resource was actually successfully acquired then we short-circuit the effect.
 *    Reason being, we cannot `release` what we did not `acquire` first. Same reason we cannot call `use`.
 *    If it is successful we pass the result to stage 2 `consumption`.
 *
 * 2. Resource consumption is like any other [Fx] effect. The key difference here is that it's wired in such a way that
 *    `release` will always be called either on `ExitCase.Cancel`, `ExitCase.Error` or `ExitCase.Complete`.
 *    If it failed than the resulting [Fx] from [FxBracket] will be `Fx.raiseError(e)`, otherwise the result of `use`.
 *
 * 3. Resource releasing is **NON CANCELABLE** otherwise it could result in leaks.
 *    In the case it throws the resulting [Fx] will be either the error or a composed error if one occurred in the [use] stage.
 *
 */
internal object FxBracket {

  /**
   * @see [Fx.bracketCase]
   */
  operator fun <A, B> invoke(acquire: FxOf<A>, release: (A, ExitCase<Throwable>) -> FxOf<Unit>, use: (A) -> FxOf<B>): Fx<B> =
    Fx.async { conn, cb ->
      val forwardCancel = FxForwardCancelable()
      conn.push(forwardCancel.cancel()) //Connect ForwardCancelable to existing connection.

      if (conn.isNotCanceled()) FxRunLoop.start(acquire, cb = BracketStart(use, release, conn, forwardCancel, cb))
      else forwardCancel.complete(Fx.unit)
    }

  private class BracketStart<A, B>(
    val use: (A) -> FxOf<B>,
    val release: (A, ExitCase<Throwable>) -> FxOf<Unit>,
    val conn: FxConnection,
    val forwardCancel: FxForwardCancelable,
    val cb: (Either<Throwable, B>) -> Unit) : (Either<Throwable, A>) -> Unit {
    //, Runnable // TODO this runs in cats-effect in a trampolined execution context for stack safety.

    private val called = AtomicBoolean(false)

    override fun invoke(ea: Either<Throwable, A>) {
      if (called.getAndSet(true)) throw IllegalStateException("callback called multiple times!")

      when (ea) {
        is Either.Right -> {
          val a = ea.b
          val frame = BracketReleaseFrame<A, B>(a, release)

          val fb: FxOf<B> = try {
            use(a)
          } catch (e: Throwable) {
            if (NonFatal(e)) Fx.raiseError(e)
            else throw e
          }

          // Registering our cancelable token ensures that in case cancellation is detected, release gets called
          forwardCancel.complete(frame.cancel)
          // Actual execution
          FxRunLoop.startCancelable(Fx.FlatMap(fb, frame, 0), conn, cb = cb)
        }
        is Either.Left -> cb(ea)
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
      FxBracket.ReleaseRecover(e),
      0
    )

    override operator fun invoke(a: B): Fx<B> = Fx.Map(
      Fx.ConnectionSwitch(applyRelease(ExitCase.Completed), Fx.ConnectionSwitch.makeUncancelable, Fx.ConnectionSwitch.disableUncancelableAndPop)
    ) { a }
  }

  internal class BracketReleaseFrame<A, B>(val a: A, val releaseFn: (A, ExitCase<Throwable>) -> FxOf<Unit>) : FxBracket.BaseReleaseFrame<A, B>() {
    override fun release(c: ExitCase<Throwable>): CancelToken<ForFx> = releaseFn(a, c)
  }

  internal class GuaranteeReleaseFrame<A>(val releaseFn: (ExitCase<Throwable>) -> FxOf<Unit>) : FxBracket.BaseReleaseFrame<Unit, A>() {
    override fun release(c: ExitCase<Throwable>): CancelToken<ForFx> = releaseFn(c)
  }
}

fun <A> FxOf<A>.guaranteeCase(release: (ExitCase<Throwable>) -> FxOf<Unit>): Fx<A> = Fx.async { conn, cb ->
  // TODO on cats-effect all this block is run using an immediate ExecutionContext for stack safety.
  val frame = FxBracket.GuaranteeReleaseFrame<A>(release)
  val onNext = Fx.FlatMap(this, frame, 0)
  // Registering our cancelable token ensures that in case cancellation is detected, `release` gets called
  conn.push(frame.cancel)

  // Race condition check, avoiding starting `source` in case the connection was already cancelled — n.b. we don't need
  // to trigger `release` otherwise, because it already happened
  if (conn.isNotCanceled()) FxRunLoop.startCancelable(onNext, conn, cb = cb)
  else Unit
}