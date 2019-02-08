package arrow.effects

import arrow.undocumented
import java.util.concurrent.ForkJoinPool
import kotlin.coroutines.*

@undocumented
// FIXME use expected and actual for multiplatform
object IODispatchers {
  // FIXME use ForkJoinPool.commonPool() in Java 8
  val CommonPool: CoroutineContext = EmptyCoroutineContext + Pool(ForkJoinPool())

  private class Pool(val pool: ForkJoinPool) : AbstractCoroutineContextElement(ContinuationInterceptor), ContinuationInterceptor {
    override fun <T> interceptContinuation(continuation: Continuation<T>): Continuation<T> =
      PoolContinuation(pool, continuation.context.fold(continuation) { cont, element ->
        if (element != this@Pool && element is ContinuationInterceptor)
          element.interceptContinuation(cont) else cont
      })
  }

  private class PoolContinuation<T>(
    val pool: ForkJoinPool,
    val cont: Continuation<T>
  ) : Continuation<T> {
    override val context: CoroutineContext = cont.context

    override fun resumeWith(result: Result<T>) {
      pool.execute { cont.resumeWith(result) }
    }
  }
}
