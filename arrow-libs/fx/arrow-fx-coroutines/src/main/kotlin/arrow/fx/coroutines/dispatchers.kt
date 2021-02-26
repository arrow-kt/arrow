package arrow.fx.coroutines

import java.util.concurrent.ExecutorService
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.Continuation
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext

internal fun ExecutorService.asCoroutineContext(): CoroutineContext =
  ExecutorServiceContext(this)

/**
 * Wraps an [ExecutorService] in a [CoroutineContext] as a [ContinuationInterceptor]
 * scheduling on the [ExecutorService] when [kotlin.coroutines.intrinsics.intercepted] is called.
 */
private class ExecutorServiceContext(val pool: ExecutorService) :
  AbstractCoroutineContextElement(ContinuationInterceptor), ContinuationInterceptor {
  override fun <T> interceptContinuation(continuation: Continuation<T>): Continuation<T> =
    ExecutorServiceContinuation(
      pool,
      continuation.context.fold(continuation) { cont, element ->
        if (element != this@ExecutorServiceContext && element is ContinuationInterceptor)
          element.interceptContinuation(cont) else cont
      }
    )
}

/** Wrap existing continuation to resumes itself on the provided [ExecutorService] */
private class ExecutorServiceContinuation<T>(val pool: ExecutorService, val cont: Continuation<T>) : Continuation<T> {
  override val context: CoroutineContext = cont.context

  override fun resumeWith(result: Result<T>) {
    pool.execute { cont.resumeWith(result) }
  }
}
