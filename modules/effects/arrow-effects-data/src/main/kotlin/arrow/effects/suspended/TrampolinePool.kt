package arrow.effects.suspended

import java.util.concurrent.ForkJoinPool
import kotlin.coroutines.*

val TrampolinePool: CoroutineContext = EmptyCoroutineContext + TrampolinePoolElement(ForkJoinPool())

val iterations: ThreadLocal<Int> = ThreadLocal.withInitial { 0 }

private class TrampolinePoolElement(
  val pool: ForkJoinPool,
  val asyncBoundaryAfter: Int = 127
) : AbstractCoroutineContextElement(ContinuationInterceptor), ContinuationInterceptor {

  override fun <T> interceptContinuation(continuation: Continuation<T>): Continuation<T> =
    TrampolinedContinuation(pool, continuation.context.fold(continuation) { cont, element ->
      if (element != this@TrampolinePoolElement && element is ContinuationInterceptor)
        element.interceptContinuation(cont) else cont
    }, asyncBoundaryAfter)
}

private class TrampolinedContinuation<T>(
  val pool: ForkJoinPool,
  val cont: Continuation<T>,
  val asyncBoundaryAfter: Int
) : Continuation<T> {
  override val context: CoroutineContext = cont.context

  override fun resumeWith(result: Result<T>) {
    if (iterations.get() > asyncBoundaryAfter) {
      iterations.set(0)
      pool.execute { cont.resumeWith(result) }
    } else {
      iterations.set(iterations.get() + 1)
      cont.resumeWith(result)
    }
  }
}
