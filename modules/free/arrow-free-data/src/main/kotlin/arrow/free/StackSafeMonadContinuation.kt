package arrow.free

import arrow.Kind
import arrow.typeclasses.Continuation
import arrow.typeclasses.Monad
import arrow.typeclasses.MonadFx
import arrow.typeclasses.MonadSyntax
import arrow.typeclasses.stateStack
import arrow.typeclasses.suspended.BindSyntax
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.RestrictsSuspension
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.startCoroutine

@RestrictsSuspension
interface StackSafeSyntax<F> : MonadSyntax<F>, FreeSyntax<F>

interface FreeSyntax<F> {
  suspend fun <B> Free<F, B>.bind(): B
}

open class StackSafeMonadContinuation<F, A>(M: Monad<F>, override val context: CoroutineContext = EmptyCoroutineContext) :
  Continuation<Free<F, A>>, Monad<F> by M, BindSyntax<F>, StackSafeSyntax<F> {

  override fun resume(value: Free<F, A>) {
    returnedMonad = value
  }

  override fun resumeWithException(exception: Throwable) {
    throw exception
  }

  private lateinit var returnedMonad: Free<F, A>

  internal fun returnedMonad(): Free<F, A> = returnedMonad

  override suspend fun <B> Kind<F, B>.bind(): B = Free.liftF(this).bind()

  override suspend fun <B> Free<F, B>.bind(): B = suspendCoroutineUninterceptedOrReturn { c ->
    val labelHere = c.stateStack // save the whole coroutine stack labels
    returnedMonad = flatMap { z ->
      c.stateStack = labelHere
      c.resumeWith(Result.success(z))
      returnedMonad
    }
    COROUTINE_SUSPENDED
  }
}

/**
 * Entry point for monad bindings which enables for comprehension. The underlying impl is based on coroutines.
 * A coroutine is initiated and inside [StackSafeMonadContinuation] suspended yielding to [flatMap]. Once all the flatMap binds are completed
 * the underlying monad is returned from the act of executing the coroutine.
 *
 * This combinator ultimately returns computations lifting to [Free] to automatically for comprehend in a stack-safe way
 * over any stack-unsafe monads.
 */
fun <F, B> MonadFx<F>.stackSafe(c: suspend StackSafeSyntax<F>.() -> B): Free<F, B> {
  val continuation = StackSafeMonadContinuation<F, B>(M)
  val wrapReturn: suspend StackSafeMonadContinuation<F, *>.() -> Free<F, B> = { Free.just(c()) }
  wrapReturn.startCoroutine(continuation, continuation)
  return continuation.returnedMonad()
}
