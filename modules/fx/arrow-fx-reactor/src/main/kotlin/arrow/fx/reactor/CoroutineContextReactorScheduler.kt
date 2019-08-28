package arrow.fx.reactor

import arrow.typeclasses.Continuation
import reactor.core.Disposable
import reactor.core.scheduler.Scheduler
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.startCoroutine

object CoroutineContextReactorScheduler {
  private interface NonCancellableContinuation : Continuation<Unit>, Disposable

  private val emptyDisposable = object : Disposable {
    override fun dispose() {}

    override fun isDisposed(): Boolean = true
  }

  fun CoroutineContext.asScheduler(): Scheduler =
    object : Scheduler {
      override fun schedule(task: Runnable): Disposable =
        createWorker().schedule(task)

      override fun createWorker(): Scheduler.Worker =
        object : Scheduler.Worker {
          override fun schedule(run: Runnable): Disposable {
            if (once) {
              return emptyDisposable
            }

            val a: suspend () -> Unit = { run.run() }
            val completion: NonCancellableContinuation = simpleContinuation(this@asScheduler)
            a.startCoroutine(completion)
            return completion
          }

          @Volatile
          var once = false

          override fun isDisposed(): Boolean = once

          override fun dispose() {
            once = false
          }

          private fun simpleContinuation(context: CoroutineContext): NonCancellableContinuation =
            object : NonCancellableContinuation {
              override fun isDisposed(): Boolean = false

              override fun dispose() {
              }

              override val context: CoroutineContext = context

              override fun resume(value: Unit) {
              }

              override fun resumeWithException(exception: Throwable) {
                throw exception
              }
            }
        }
    }
}
