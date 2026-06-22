package arrow.continuations

import arrow.autoCloseScope
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Duration
import kotlinx.coroutines.*

/**
 * Scope for [SuspendApp], with operations that are specific to the application lifecycle.
 */
public class SuspendAppScope internal constructor(
  private val coroutineScope: CoroutineScope,
) : CoroutineScope by coroutineScope

/**
 * An unsafe blocking edge that wires the [SuspendAppScope] (and structured concurrency) to the
 * [SuspendApp], such that the [SuspendAppScope] gets cancelled when the `App` is requested to
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
  block: suspend SuspendAppScope.() -> Unit,
): Unit = autoCloseScope {
  val env = process()
  env.runScope(context) {
    val result = supervisorScope {
      val app =
        async(start = CoroutineStart.LAZY) {
          val appScope = SuspendAppScope(this)
          appScope.block()
        }
      val unregister =
        env.onShutdown {
          withTimeout(timeout) {
            app.cancel(SuspendAppShutdown())
            app.join()
          }
        }
      runCatching { app.await() }
        .also { unregister() }
    }
    result.fold({ env.exit(0) }) { e ->
      when (e) {
        is SuspendAppShutdown -> env.exit(e.code ?: -1)
        else -> {
          uncaught(e)
          env.exit(-1)
        }
      }
    }
  }
}

/**
 * Gracefully exits the enclosing [SuspendApp] with process exit status [code].
 *
 * This signals [SuspendApp] to complete with the given process exit status, while allowing
 * resource finalizers to run before [SuspendApp] exits the process. On Unix-like systems, this
 * status is reported to the parent process or shell; `0` conventionally denotes success, and
 * non-zero values denote failure or application-specific outcomes.
 *
 * To be used as a replacement for `System.exit(code)` and for `kotlin.system.exitProcess(code)`,
 * which are unsafe to use in a coroutines-driven `main`, leading to deadlocks.
 *
 * @param code the exit status of the process.
 */
public fun SuspendAppScope.exitApp(code: Int) {
  val shutdown = SuspendAppShutdown(code)
  coroutineContext.cancel(shutdown)
  throw shutdown
}

/** Marker type to track the shutdown signal */
private class SuspendAppShutdown(val code: Int? = null) :
  CancellationException(code?.let { "SuspendApp exiting with code $it." } ?: "SuspendApp shutting down.")
