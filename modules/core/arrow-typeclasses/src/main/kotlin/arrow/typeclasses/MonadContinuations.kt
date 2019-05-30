package arrow.typeclasses

import arrow.Kind
import arrow.core.Continuation
import arrow.typeclasses.suspended.BindSyntax
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.RestrictsSuspension
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@RestrictsSuspension
interface MonadContext<F> : Monad<F>, BindSyntax<F>

@RestrictsSuspension
open class MonadContinuation<F, A>(M: Monad<F>, override val context: CoroutineContext = EmptyCoroutineContext) :
  Continuation<Kind<F, A>>, Monad<F> by M, BindSyntax<F>, MonadContext<F> {

  override fun resume(value: Kind<F, A>) {
    returnedMonad = value
  }

  @Suppress("UNCHECKED_CAST")
  override fun resumeWithException(exception: Throwable) {
    when (exception) {
      is ContinuationShortcircuitThrowable -> returnedMonad = exception.exit as Kind<F, A>
      else -> throw exception
    }
  }

  protected lateinit var returnedMonad: Kind<F, A>

  open fun returnedMonad(): Kind<F, A> = returnedMonad

  override suspend fun <B> Kind<F, B>.bind(): B =
    when (val r = bindStrategy(this)) {
      is BindingStrategy.MultiShot -> suspendCoroutineUninterceptedOrReturn { c ->
        val labelHere = c.stateStack // save the whole coroutine stack labels
        returnedMonad = this.flatMap { x: B ->
          c.stateStack = labelHere
          c.resume(x)
          returnedMonad
        }
        COROUTINE_SUSPENDED
      }
      is BindingStrategy.ContinuationShortCircuit -> suspendCoroutineUninterceptedOrReturn { c ->
        c.resumeWithException(r.throwable)
        COROUTINE_SUSPENDED
      }
      is BindingStrategy.Strict -> r.a
      is BindingStrategy.Suspend -> r.f()
    }
}
