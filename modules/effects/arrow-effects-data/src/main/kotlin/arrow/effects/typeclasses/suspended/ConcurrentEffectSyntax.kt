package arrow.effects.typeclasses.suspended

import arrow.Kind
import arrow.core.Either
import arrow.effects.typeclasses.ConcurrentEffect
import arrow.effects.typeclasses.Disposable

interface ConcurrentEffectSyntax<F> : ConcurrentEffect<F>, EffectSyntax<F> {

  private suspend fun <A> concurrentEffect(fb: ConcurrentEffect<F>.() -> Kind<F, A>): A =
    run<ConcurrentEffect<F>, Kind<F, A>> { fb(this) }.bind()

  suspend fun <A> (suspend () -> A).runAsyncCancellable(cb: suspend (Either<Throwable, A>) -> Unit): Disposable =
    concurrentEffect { this@runAsyncCancellable.k().runAsyncCancellable(cb.kr()) }

}