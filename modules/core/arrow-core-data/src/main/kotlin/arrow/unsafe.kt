package arrow

import arrow.typeclasses.Continuation
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.RestrictsSuspension
import kotlin.coroutines.startCoroutine

private class UnsafeContinuation<A>(
  val result: AtomicReference<A> = AtomicReference()
) : Continuation<A> {

  override fun resume(value: A) {
    result.set(value)
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
    return c.result.get()
  }
}
