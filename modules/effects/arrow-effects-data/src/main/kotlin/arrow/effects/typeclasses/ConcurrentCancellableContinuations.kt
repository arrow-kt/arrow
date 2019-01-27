package arrow.effects.typeclasses

import arrow.Kind
import arrow.effects.typeclasses.suspended.ConcurrentSyntax
import arrow.typeclasses.MonadContinuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.RestrictsSuspension

@RestrictsSuspension
@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
open class ConcurrentCancellableContinuation<F, A>(CF: Concurrent<F>, override val context: CoroutineContext = EmptyCoroutineContext) :
  MonadDeferCancellableContinuation<F, A>(CF), Concurrent<F> by CF, ConcurrentSyntax<F> {

  override fun <B> binding(c: suspend MonadContinuation<F, *>.() -> B): Kind<F, B> =
    bindingCancellable { c() }.a

  override fun <A> f(fa: suspend () -> A): Kind<F, A> =
    super<ConcurrentSyntax>.f(fa)

}
