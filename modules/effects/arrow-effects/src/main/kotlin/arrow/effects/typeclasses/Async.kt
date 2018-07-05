package arrow.effects.typeclasses

import arrow.Kind
import arrow.core.Either
import arrow.typeclasses.MonadContinuation
import kotlin.coroutines.experimental.CoroutineContext

/** An asynchronous computation that might fail. **/
typealias Proc<A> = ((Either<Throwable, A>) -> Unit) -> Unit

/** The context required to run an asynchronous computation that may fail. **/
interface Async<F> : MonadDefer<F> {
  fun <A> async(fa: Proc<A>): Kind<F, A>

  fun <A> Kind<F, A>.continueOn(ctx: CoroutineContext): Kind<F, A>

  suspend fun <A> MonadContinuation<F, A>.continueOn(ctx: CoroutineContext): Unit =
    just(Unit).continueOn(ctx).bind()

  fun <A> never(): Kind<F, A> =
    async { }
}
