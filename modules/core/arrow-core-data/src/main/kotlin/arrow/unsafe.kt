package arrow

import arrow.typeclasses.Continuation
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.RestrictsSuspension
import kotlin.coroutines.startCoroutine

private class UnsafeContinuation<A> : Continuation<A> {
  val result: AtomicRef<A?> = atomic(null)

  override fun resume(value: A) {
    result.value = value
  }

  override fun resumeWithException(exception: Throwable) {
    throw exception
  }

  override val context: CoroutineContext = EmptyCoroutineContext
}

@RestrictsSuspension
object unsafe {

  operator fun <A> invoke(f: suspend unsafe.() -> A): A {
    val c = UnsafeContinuation<A>()
    f.startCoroutine(this, c)
    return c.result.value!!
  }
}
