package arrow

import arrow.core.Continuation
import java.util.concurrent.CountDownLatch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.RestrictsSuspension
import kotlin.coroutines.startCoroutine

private class UnsafeContinuation<A> : Continuation<A> {
  val latch = CountDownLatch(1)
  var ref: A? = null

  override fun resume(value: A) {
    latch.countDown()
    ref = value
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
    c.latch.await()
    return c.ref!!
  }

  suspend fun <A> suspendCoroutine(cont: (kotlin.coroutines.Continuation<A>) -> Unit): A =
    kotlin.coroutines.suspendCoroutine(cont)

}