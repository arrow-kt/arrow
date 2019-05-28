package arrow.effects.typeclasses

import arrow.Kind
import arrow.typeclasses.MonadContinuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.RestrictsSuspension

@RestrictsSuspension
@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
open class ConcurrentContinuation<F, A>(CF: Concurrent<F>, override val context: CoroutineContext = EmptyCoroutineContext) :
  AsyncContinuation<F, A>(CF), Concurrent<F> by CF, FxSyntax<F> {
  override val fx: ConcurrentFx<F> = CF.fx
  override fun <B> binding(c: suspend MonadContinuation<F, *>.() -> B): Kind<F, B> = fx.monad(c)

  override fun <A> async(fa: Proc<A>): Kind<F, A> =
    super<FxSyntax>.async(fa)

  override fun <A> asyncF(k: ProcF<F, A>): Kind<F, A> =
    super<FxSyntax>.asyncF(k)
}
