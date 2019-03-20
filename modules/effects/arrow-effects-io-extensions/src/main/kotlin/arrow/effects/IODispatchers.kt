package arrow.effects

import arrow.effects.internal.Trampoline
import arrow.undocumented
import java.util.concurrent.Executor
import java.util.concurrent.ForkJoinPool
import kotlin.concurrent.getOrSet
import kotlin.coroutines.*

@undocumented
// FIXME use expected and actual for multiplatform
object IODispatchers {
  // FIXME use ForkJoinPool.commonPool() in Java 8
  val CommonPool: CoroutineContext = Pool(ForkJoinPool())

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

  fun TrampolinePool(executorService: Executor): CoroutineContext =
    TrampolinePoolElement(executorService)

  private val iterations: ThreadLocal<Int> = ThreadLocal.withInitial { 0 }
  private val threadTrampoline = ThreadLocal<Trampoline>()

  private class TrampolinePoolElement(
    val executionService: Executor,
    val asyncBoundaryAfter: Int = 127
  ) : AbstractCoroutineContextElement(ContinuationInterceptor), ContinuationInterceptor {

    override fun <T> interceptContinuation(continuation: Continuation<T>): Continuation<T> =
      TrampolinedContinuation(executionService, continuation.context.fold(continuation) { cont, element ->
        if (element != this@TrampolinePoolElement && element is ContinuationInterceptor)
          element.interceptContinuation(cont)
        else cont
      }, asyncBoundaryAfter)
  }

  private class TrampolinedContinuation<T>(
    val executionService: Executor,
    val cont: Continuation<T>,
    val asyncBoundaryAfter: Int
  ) : Continuation<T> {
    override val context: CoroutineContext = cont.context

    override fun resumeWith(result: Result<T>) {
      if (iterations.get() > asyncBoundaryAfter) {
        iterations.set(0)
        println("Jumping async. cont.context: $context")
        threadTrampoline
          .getOrSet { Trampoline(executionService) }
          .execute(Runnable {
            cont.resumeWith(result)
          })
      } else {
        iterations.set(iterations.get() + 1)
        cont.resumeWith(result)
      }
    }
  }
}
