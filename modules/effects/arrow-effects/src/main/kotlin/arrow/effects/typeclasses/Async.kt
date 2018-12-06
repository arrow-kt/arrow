package arrow.effects.typeclasses

import arrow.Kind
import arrow.core.Either
import arrow.typeclasses.MonadContinuation
import kotlin.coroutines.CoroutineContext

/** An asynchronous computation that might fail. **/
typealias Proc<A> = ((Either<Throwable, A>) -> Unit) -> Unit

typealias ProcF<F, A> = ((Either<Throwable, A>) -> Unit) -> Kind<F, Unit>

/**
 * ank_macro_hierarchy(arrow.effects.typeclasses.Async)
 *
 * The context required to run an asynchronous computation that may fail.
 **/
interface Async<F> : MonadDefer<F> {
  fun <A> async(fa: Proc<A>): Kind<F, A>

  /**
   * [[async]] variant that can suspend side effects in the provided registration function. On this variant, the passed
   * in function is injected with a side-effectful callback for signaling the final result of an asynchronous process.
   * Its returned result needs to be a pure `F[Unit]` that gets evaluated by the runtime.
   */
  fun <A> asyncF(k: ProcF<F, A>): Kind<F, A> = TODO()

  fun <A> Kind<F, A>.continueOn(ctx: CoroutineContext): Kind<F, A>

  operator fun <A> invoke(ctx: CoroutineContext, f: () -> A): Kind<F, A> =
    lazy().continueOn(ctx).flatMap { delay(f) }

  fun <A> defer(ctx: CoroutineContext, f: () -> Kind<F, A>): Kind<F, A> =
    lazy().continueOn(ctx).flatMap { defer(f) }

  suspend fun <A> MonadContinuation<F, A>.continueOn(ctx: CoroutineContext): Unit =
    just(Unit).continueOn(ctx).bind()

  fun <A> never(): Kind<F, A> =
    async { }
}
