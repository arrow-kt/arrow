package arrow.fx.typeclasses

import arrow.Kind
import arrow.typeclasses.MonadSyntax
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.RestrictsSuspension

@RestrictsSuspension
interface ConcurrentSyntax<F, E> : Concurrent<F, Throwable>, AsyncSyntax<F, Throwable>, FxSyntax<F>

@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
open class ConcurrentContinuation<F, A, E>(private val CF: Concurrent<F, E>, override val context: CoroutineContext = EmptyCoroutineContext) :
  AsyncContinuation<F, A, E>(CF), Concurrent<F, E> by CF, ConcurrentSyntax<F, E> {
  override val fx: ConcurrentFx<F, E> = CF.fx
  override fun <B> binding(c: suspend MonadSyntax<F>.() -> B): Kind<F, B> = fx.monad(c)
}
