package arrow.continuations

import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Duration
import kotlinx.coroutines.*
import kotlin.coroutines.Continuation
import kotlin.coroutines.startCoroutine

/**
 * An unsafe blocking edge that wires the [CoroutineScope] (and structured concurrency) to the
 * [SuspendApp], such that the [CoroutineScope] gets cancelled when the `App` is requested to
 * gracefully shutdown. => `SIGTERM` & `SIGINT` on Native & NodeJS and a ShutdownHook for JVM.
 *
 * It applies backpressure to the process such that they can gracefully shutdown.
 *
 * @param context the [CoroutineContext] where [block] will execute. Use [EmptyCoroutineContext] to
 *   create an `CoroutineDispatcher` for the main thread and run there instead.
 * @param timeout the maximum backpressure time that can be applied to the process. This emulates a
 *   `SIGKILL` command, and after the [timeout] is passed the App will forcefully shut down
 *   regardless of finalizers.
 * @param block the lambda of the actual application.
 */
public fun SuspendApp(
  context: CoroutineContext = Dispatchers.Default,
  uncaught: (Throwable) -> Unit = Throwable::printStackTrace,
  timeout: Duration = Duration.INFINITE,
  process: Process = process(),
  block: suspend CoroutineScope.() -> Unit,
): Unit =
  process.use { env ->
    val jobCause = CompletableDeferred<Throwable?>()
    env.runScope(context, { jobCause.complete(it.exceptionOrNull()) }) {
      val job =
        launch(start = CoroutineStart.LAZY) {
          try {
            block()
          } catch (_: SuspendAppShutdown) {} catch (e: Throwable) {
            throw e
          }
        }
      val unregister =
        env.onShutdown {
          withTimeout(timeout) {
            job.cancel(SuspendAppShutdown)
            job.join()
          }
        }
      job.start()
      job.join()
      unregister()
    }

    suspend { jobCause.join() }
      .startCoroutine(Continuation(EmptyCoroutineContext) {
      check(jobCause.isCompleted)
      @OptIn(ExperimentalCoroutinesApi::class)
      when (val cause = jobCause.getCompleted()) {
        is SuspendAppShutdown, null -> env.exit(0)
        else -> {
          uncaught(cause)
          env.exit(-1)
        }
      }
    })
  }

/** Marker type so track shutdown signal */
private object SuspendAppShutdown : CancellationException("SuspendApp shutting down.")
