package arrow.continuations

import arrow.autoCloseScope
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.coroutineContext
import kotlin.time.Duration
import kotlinx.coroutines.*

/**
 * Gracefully exits the enclosing [SuspendApp] with [code].
 *
 * This signals [SuspendApp] to complete with [code], allowing `finally` blocks and resource
 * finalizers to run before [SuspendApp] exits the process.
 *
 * This function is intended to be used from inside [SuspendApp], where it is interpreted as a
 * graceful exit with [code].
 */
public suspend fun CoroutineScope.exitApp(code: Int): Nothing {
  check(coroutineContext[SuspendAppContextKey] != null) {
    "arrow.continuations.exitApp can only be used inside SuspendApp"
  }
  throw SuspendAppShutdown(code)
}

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
  block: suspend CoroutineScope.() -> Unit,
): Unit = autoCloseScope {
  val env = process()
  env.runScope(context) {
    val result = supervisorScope {
      val app =
        async(context = SuspendAppContext, start = CoroutineStart.LAZY, block = block)
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
        is SuspendAppShutdown -> e.code?.let(env::exit)
        else -> {
          uncaught(e)
          env.exit(-1)
        }
      }
    }
  }
}

/** Marker type to track shutdown signal */
private class SuspendAppShutdown(val code: Int? = null) :
  CancellationException(code?.let { "SuspendApp exiting with code $it." } ?: "SuspendApp shutting down.")

private object SuspendAppContextKey : CoroutineContext.Key<SuspendAppContext>

private object SuspendAppContext : CoroutineContext.Element {
  override val key: CoroutineContext.Key<*> = SuspendAppContextKey
}
