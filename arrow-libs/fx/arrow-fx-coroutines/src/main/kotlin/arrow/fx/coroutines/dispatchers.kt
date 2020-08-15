package arrow.fx.coroutines

import kotlinx.atomicfu.atomic
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ForkJoinPool
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.Continuation
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext

/**
 * A [CoroutineContext] to run non-blocking suspending code,
 * all code that relies on blocking IO should prefer to use an unbounded [IOPool].
 *
 * A work-stealing thread pool using all available processors as its target parallelism level.
 */
val ComputationPool: CoroutineContext =
  ForkJoinPool().asCoroutineContext()

private object IOCounter {
  private val ref = atomic(0)
  fun getAndIncrement(): Int = ref.getAndIncrement()
}

/**
 * Creates a thread pool that creates new threads as needed, but
 * will reuse previously constructed threads when they are available, and uses the provided.
 *
 * This pool is prone to cause [OutOfMemoryError] since the pool size is unbounded.
 */
val IOPool: CoroutineContext =
  Executors.newCachedThreadPool { r ->
    Thread(r).apply {
      name = "io-arrow-kt-worker-${IOCounter.getAndIncrement()}"
      isDaemon = true
    }
  }.asCoroutineContext()

internal fun ExecutorService.asCoroutineContext(): CoroutineContext =
  ExecutorServiceContext(this)

/**
 * Wraps an [ExecutorService] in a [CoroutineContext] as a [ContinuationInterceptor]
 * scheduling on the [ExecutorService] when [kotlin.coroutines.intrinsics.intercepted] is called.
 */
private class ExecutorServiceContext(val pool: ExecutorService) :
  AbstractCoroutineContextElement(ContinuationInterceptor), ContinuationInterceptor {
  override fun <T> interceptContinuation(continuation: Continuation<T>): Continuation<T> =
    ExecutorServiceContinuation(pool, continuation.context.fold(continuation) { cont, element ->
      if (element != this@ExecutorServiceContext && element is ContinuationInterceptor)
        element.interceptContinuation(cont) else cont
    })
}

/** Wrap existing continuation to resumes itself on the provided [ExecutorService] */
private class ExecutorServiceContinuation<T>(val pool: ExecutorService, val cont: Continuation<T>) : Continuation<T> {
  override val context: CoroutineContext = cont.context

  override fun resumeWith(result: Result<T>) {
    pool.execute { cont.resumeWith(result) }
  }
}
