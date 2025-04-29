package arrow.continuations

import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Duration
import kotlinx.coroutines.*

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
@OptIn(ExperimentalStdlibApi::class)  // 'use' in stdlib < 2.0
public fun SuspendApp(
  context: CoroutineContext = Dispatchers.Default,
  uncaught: (Throwable) -> Unit = Throwable::printStackTrace,
  timeout: Duration = Duration.INFINITE,
  block: suspend CoroutineScope.() -> Unit,
): Unit =
  process().use { env ->
    env.runScope(context) {
      val result = supervisorScope {
        val app =
          async(start = CoroutineStart.LAZY, block = block)
        val unregister =
          env.onShutdown {
            withTimeout(timeout) {
              app.cancel(SuspendAppShutdown)
              app.join()
            }
          }
        runCatching { app.await() }
          .also { unregister() }
      }
      result.fold({ env.exit(0) }) { e ->
        if (e !is SuspendAppShutdown) {
          uncaught(e)
          env.exit(-1)
        }
      }
    }
  }

/** Marker type so track shutdown signal */
private object SuspendAppShutdown : CancellationException("SuspendApp shutting down.")
