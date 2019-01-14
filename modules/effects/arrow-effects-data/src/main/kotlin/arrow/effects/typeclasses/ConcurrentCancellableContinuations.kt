package arrow.effects.typeclasses

import arrow.Kind
import arrow.effects.typeclasses.suspended.ConcurrentSyntax
import arrow.typeclasses.Continuation
import arrow.typeclasses.MonadContinuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.RestrictsSuspension
import kotlin.coroutines.startCoroutine

typealias ConcurrentEffects<F> = ConcurrentCancellableContinuation<F, *>

@RestrictsSuspension
@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
open class ConcurrentCancellableContinuation<F, A>(val CF: Concurrent<F>, override val context: CoroutineContext = EmptyCoroutineContext) :
  MonadDeferCancellableContinuation<F, A>(CF), Concurrent<F> by CF, ConcurrentSyntax<F> {
  override fun <B> binding(c: suspend MonadContinuation<F, *>.() -> B): Kind<F, B> =
    bindingCancellable { c() }.a
}

class SyncContinuation<A: Any> : Continuation<A> {

  lateinit var result: A

  override fun resume(value: A) {
    result = value
  }

  override fun resumeWithException(exception: Throwable) {
    throw exception
  }

  override val context: CoroutineContext = EmptyCoroutineContext
}

