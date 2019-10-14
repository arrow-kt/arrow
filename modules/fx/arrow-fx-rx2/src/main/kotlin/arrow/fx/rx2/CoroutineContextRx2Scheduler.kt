package arrow.fx.rx2

import arrow.typeclasses.Continuation
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import io.reactivex.internal.disposables.EmptyDisposable
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.startCoroutine

object CoroutineContextRx2Scheduler {
  private interface NonCancellableContinuation : Continuation<Unit>, Disposable

  fun CoroutineContext.asScheduler(): Scheduler =
    object : Scheduler() {
      override fun createWorker(): Worker =
        object : Worker() {
          @Volatile
          var once = false

          override fun isDisposed(): Boolean = once

          override fun schedule(run: Runnable, delay: Long, unit: TimeUnit): Disposable {
            if (once) {
              return EmptyDisposable.INSTANCE
            }

            val a: suspend () -> Unit = { run.run() }
            val completion: NonCancellableContinuation = simpleContinuation(this@asScheduler)
            a.startCoroutine(completion)
            return completion
          }

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
