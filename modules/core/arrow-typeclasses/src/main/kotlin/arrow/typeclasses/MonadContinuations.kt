package arrow.typeclasses

import arrow.Kind
import arrow.core.Continuation
import arrow.typeclasses.suspended.BindSyntax
import java.util.concurrent.CountDownLatch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.RestrictsSuspension
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

interface BindingInContextContinuation<in T> : Continuation<T> {
  fun await(): Throwable?
}

@RestrictsSuspension
open class MonadContinuation<F, A>(M: Monad<F>, override val context: CoroutineContext = EmptyCoroutineContext) :
  Continuation<Kind<F, A>>, Monad<F> by M, BindSyntax<F> {

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

  protected fun bindingInContextContinuation(context: CoroutineContext): BindingInContextContinuation<Kind<F, A>> =
    object : BindingInContextContinuation<Kind<F, A>> {
      val latch: CountDownLatch = CountDownLatch(1)

      var error: Throwable? = null

      override fun await() = latch.await().let { error }

      override val context: CoroutineContext = context

      override fun resume(value: Kind<F, A>) {
        returnedMonad = value
        latch.countDown()
      }

      override fun resumeWithException(exception: Throwable) {
        error = exception
        latch.countDown()
      }
    }

  protected lateinit var returnedMonad: Kind<F, A>

  open fun returnedMonad(): Kind<F, A> = returnedMonad

  override suspend fun <B> Kind<F, B>.bind(): B =
    when (val strategy = bindStrategy(this)) {
      is BindingStrategy.MultiShot -> suspendCoroutineUninterceptedOrReturn { c ->
        val labelHere = c.stateStack // save the whole coroutine stack labels
        returnedMonad = this.flatMap { x: B ->
          c.stateStack = labelHere
          c.resume(x)
          returnedMonad
        }
        COROUTINE_SUSPENDED
      }
      is BindingStrategy.Strict -> strategy.a
      is BindingStrategy.ContinuationShortCircuit -> suspendCoroutineUninterceptedOrReturn { c ->
        c.resumeWithException(strategy.throwable)
        COROUTINE_SUSPENDED
      }
      is BindingStrategy.Suspend -> strategy.f()
    }
}
