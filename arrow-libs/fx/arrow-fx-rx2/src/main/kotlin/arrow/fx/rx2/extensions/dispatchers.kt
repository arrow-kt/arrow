package arrow.fx.rx2.extensions

import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.Continuation
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext

internal val ComputationScheduler: CoroutineContext =
  Schedulers.computation().asCoroutineContext()

internal val IOScheduler: CoroutineContext =
  Schedulers.io().asCoroutineContext()

fun Scheduler.asCoroutineContext(): CoroutineContext =
  SchedulerContext(this)

private class SchedulerContext(val scheduler: Scheduler) : AbstractCoroutineContextElement(ContinuationInterceptor), ContinuationInterceptor {
  override fun <T> interceptContinuation(continuation: Continuation<T>): Continuation<T> =
    SchedulerContinuation(scheduler, continuation.context.fold(continuation) { cont, element ->
      if (element != this@SchedulerContext && element is ContinuationInterceptor)
        element.interceptContinuation(cont) else cont
    })
}

private class SchedulerContinuation<T>(
  val scheduler: Scheduler,
  val cont: Continuation<T>
) : Continuation<T> {
  override val context: CoroutineContext = cont.context

  override fun resumeWith(result: Result<T>) {
    scheduler.scheduleDirect { cont.resumeWith(result) }
  }
}
