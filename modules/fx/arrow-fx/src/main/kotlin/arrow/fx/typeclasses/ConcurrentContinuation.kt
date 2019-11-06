package arrow.fx.typeclasses

import arrow.Kind
import arrow.fx.MVar
import arrow.fx.Promise
import arrow.fx.Semaphore
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.RestrictsSuspension

@RestrictsSuspension
interface ConcurrentSyntax<F> : Concurrent<F>, AsyncSyntax<F>

@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
open class ConcurrentContinuation<F, A>(private val CF: Concurrent<F>, override val context: CoroutineContext = EmptyCoroutineContext) :
  AsyncContinuation<F, A>(CF), Concurrent<F> by CF, ConcurrentSyntax<F> {
  override val fx: ConcurrentFx<F> = CF.fx
  override fun <A> Promise(): Kind<F, Promise<F, A>> = CF.Promise()
  override fun <A> MVar(): Kind<F, MVar<F, A>> = CF.MVar()
  override fun <A> MVar(a: A): Kind<F, MVar<F, A>> = CF.MVar(a)
  override fun Semaphore(n: Long): Kind<F, Semaphore<F>> = CF.Semaphore(n)
}
