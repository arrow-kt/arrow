//package arrow.effects.suspended.fx
//
//import arrow.core.Either
//import arrow.core.NonFatal
//import arrow.effects.*
//import arrow.effects.internal.IOBracket
//import arrow.effects.internal.Platform
//import arrow.effects.typeclasses.ExitCase
//import java.util.concurrent.atomic.AtomicBoolean
//
//internal object FxBracket {
//
//  operator fun <A, B> invoke(
//    acquire: FxOf<A>,
//    release: (A, ExitCase<Throwable>) -> FxOf<Unit>,
//    use: (A) -> FxOf<B>
//  ): Fx<B> = Fx.async { conn, cb ->
//    val forwardCancel = FxForwardCancelable()
//    conn.push(forwardCancel.cancel())
//
//    if (conn.isNotCanceled()) TODO()
//    else forwardCancel.complete(Fx.unit)
//  }
//
//  private class ReleaseRecover(val error: Throwable) : FxFrame<Unit, Fx<Nothing>> {
//    override fun recover(e: Throwable): Fx<Nothing> = Fx.raiseError(Platform.composeErrors(error, e))
//    override fun invoke(a: Unit): Fx<Nothing> = Fx.raiseError(error)
//  }
//
//  private abstract class BaseReleaseFrame<A, B> : FxFrame<B, Fx<B>> {
//
//    // Guard used for thread-safety, to ensure the idempotency
//    // of the release; otherwise `release` can be called twice
//    private val waitsForResult = AtomicBoolean(true)
//
//    abstract fun release(c: ExitCase<Throwable>): CancelToken<ForFx>
//
//    private fun applyRelease(e: ExitCase<Throwable>): Fx<Unit> =
//      Fx.defer {
//        if (waitsForResult.compareAndSet(true, false))
//          release(e)
//        else
//          Fx.unit
//      }
//
//    val cancel: CancelToken<ForIO> = applyRelease(ExitCase.Canceled).fix().uncancelable()
//
//    // Unregistering cancel token, otherwise we can have a memory leak;
//    // N.B. conn.pop() happens after the evaluation of `release`, because
//    // otherwise we might have a conflict with the auto-cancellation logic
//    override fun recover(e: Throwable): IO<B> = IO.ConnectionSwitch(applyRelease(ExitCase.Error(e)), IOBracket.makeUncancelable, IOBracket.disableUncancelableAndPop)
//      .flatMap(IOBracket.ReleaseRecover(e))
//
//    override operator fun invoke(a: B): IO<B> =
//    // Unregistering cancel token, otherwise we can have a memory leak
//    // N.B. conn.pop() happens after the evaluation of `release`, because
//    // otherwise we might have a conflict with the auto-cancellation logic
//      IO.ConnectionSwitch(applyRelease(ExitCase.Completed), IOBracket.makeUncancelable, IOBracket.disableUncancelableAndPop)
//        .map { a }
//  }
//
//  private class BracketStart<A, B>(
//    val use: (A) -> FxOf<B>,
//    val release: (A, ExitCase<Throwable>) -> FxOf<Unit>,
//    val conn: FxConnection,
//    val forwardCancel: FxForwardCancelable,
//    val cb: (Either<Throwable, B>) -> Unit) : (Either<Throwable, A>) -> Unit, Runnable {
//
//    private var result: Either<Throwable, A>? = null
//
//    override fun invoke(ea: Either<Throwable, A>) {
//      if (result != null) {
//        throw IllegalStateException("callback called multiple times!")
//      }
//      // Introducing a light async boundary, otherwise executing the required
//      // logic directly will yield a StackOverflowException
//      result = ea
//      this.run() // TODO this runs in cats-effect in a trampolined execution context for stack safety.
//    }
//
//    override fun run() = result!!.let { result ->
//      when (result) {
//        is Either.Right -> {
//          val a = result.b
//          val frame = IOBracket.BracketReleaseFrame<A, B>(a, release)
//          val onNext = {
//            val fb = try {
//              use(a)
//            } catch (e: Throwable) {
//              if (NonFatal(e)) {
//                IO.raiseError<B>(e)
//              } else {
//                throw e
//              }
//            }
//
//            IO.Bind(fb.fix(), frame)
//          }
//
//          // Registering our cancelable token ensures that in case cancellation is detected, release gets called
//          forwardCancel.complete(frame.cancel)
//          // Actual execution
////          IORunLoop.startCancelable(onNext(), conn, cb)
//        }
//        is Either.Left -> cb(result)
//      }
//    }
//  }
//
//}