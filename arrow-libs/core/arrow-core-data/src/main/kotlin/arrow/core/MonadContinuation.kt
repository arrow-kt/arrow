package arrow.core

import arrow.Kind
import arrow.typeclasses.suspended.BindSyntax
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.RestrictsSuspension
import kotlin.coroutines.intrinsics.startCoroutineUninterceptedOrReturn

@RestrictsSuspension
interface EagerBind<F> : BindSyntax<F>

@PublishedApi
internal class ShortCircuit(val value: Any?) : RuntimeException(null, null) {
  override fun fillInStackTrace(): Throwable = this
  override fun toString(): String = "ShortCircuit($value)"
}

@Suppress("UNCHECKED_CAST")
internal abstract class MonadContinuation<F, A> : Continuation<Kind<F, A>>, EagerBind<F> {

  abstract fun ShortCircuit.recover(): Kind<F, A>

  override val context: CoroutineContext = EmptyCoroutineContext

  protected lateinit var returnedMonad: Kind<F, A>

  fun returnedMonad(): Kind<F, A> = returnedMonad

  override fun resumeWith(result: Result<Kind<F, A>>) {
    result.fold({ returnedMonad = it }, { e ->
      if (e is ShortCircuit) {
        returnedMonad = e.recover()
      } else throw e
    })
  }

  fun startCoroutineUninterceptedAndReturn(f: suspend EagerBind<F>.() -> Kind<F, A>): Any? =
    try {
      f.startCoroutineUninterceptedOrReturn(this, this) as Kind<F, A>
    } catch (e: Throwable) {
      if (e is ShortCircuit) e.recover()
      else throw e
    }
}
