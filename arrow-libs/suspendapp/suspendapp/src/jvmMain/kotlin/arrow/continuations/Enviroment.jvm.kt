package arrow.continuations

import arrow.AutoCloseScope
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.CoroutineContext
import kotlin.system.exitProcess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import sun.misc.Signal
import sun.misc.SignalHandler

internal actual fun AutoCloseScope.process(): Process = JvmProcess

private object JvmProcess : Process {
  override fun onShutdown(block: suspend () -> Unit): () -> Unit {
    val isShutdown = AtomicBoolean(false)
    val lastSignal = AtomicInteger(-1)
    onSigInt(lastSignal::set)
    onSigTerm(lastSignal::set)

    fun shutdown() {
      if (!isShutdown.getAndSet(true)) {
        /*
        `SuspendApp` is intended to be invoked from the main entrypoint (a non-daemon thread) and, in that normal usage,
        will unregister this hook before main completes.

        This hook can run when:
        1) JVM receives SIGINT/SIGTERM
        2) last non-daemon thread exits
        3) `System.exit` is called

        We run graceful shutdown only for (1), detected by a captured signal.

        Regarding (2):
        - In the expected usage, a non-daemon caller thread (usually main) remains alive while
          `SuspendApp` is running.
        - So if this hook is invoked because the last non-daemon thread exited, that likely reflects an
          unexpected invocation pattern (for example, daemon-thread-driven usage).
        - We choose not to provide graceful-shutdown guarantees for that edge case.

        Regarding (3) and deadlock risk:
        - `System.exit` may be called from code running in the `SuspendApp` coroutine scope.
        - `System.exit` will never return to that caller.
        - If this hook attempts graceful termination (`block()`), it can end up waiting for the
          `SuspendApp` scope to complete (join/drain), while that scope is waiting on a call path that
          includes the non-returning `System.exit`.
        - Since that wait cannot resolve, the shutdown hook will deadlock.

        Other platform behaviour:
        - Other targets do not have a JVM-style shutdown hook with equivalent process-exit semantics,
          and currently do not attempt graceful shutdown either, except when signalled.

        So, graceful shutdown is gated solely on SIGINT/SIGTERM capture, both to align behaviour with
        other platforms and to avoid the potential deadlock of an explicit `System.exit`.
        */
        if (lastSignal.get() != -1) {
          runBlocking {
            // We don't call exit from ShutdownHook on JVM
            try {
              block()
            } catch (e: Throwable) {
              e.printStackTrace()
            }
          }
        } else {
          System.err.println("WARNING: SuspendApp Shutdown Hook invoked without being signalled. No SuspendApp cancellation will occur.")
        }
      }
    }

    val hook = Thread(::shutdown, "Arrow-kt SuspendApp JVM ShutdownHook")
    Runtime.getRuntime().addShutdownHook(hook)
    return {
      if (!isShutdown.get()) {
        try {
          Runtime.getRuntime().removeShutdownHook(hook)
        } catch (_: IllegalStateException) {
          // Shutdown hook already running, ignore
        }
      }
    }
  }

  override fun onSigTerm(block: suspend (code: Int) -> Unit): Unit =
    addSignalHandler("TERM", block)

  override fun onSigInt(block: suspend (code: Int) -> Unit): Unit =
    addSignalHandler("INT", block)

  private fun addSignalHandler(signal: String, action: suspend (code: Int) -> Unit): Unit =
    try {
      var handle: SignalHandler? = null
      handle =
        Signal.handle(Signal(signal)) { sig ->
          runBlocking { action(sig.number) }
          if (handle != SignalHandler.SIG_DFL && handle != SignalHandler.SIG_IGN) {
            handle?.handle(sig)
          }
        }
    } catch (ex: Throwable) {
      System.err.println("WARNING: SuspendApp was unable to install a signal handler for SIG$signal. Signalled cancellation may be impacted.")
      ex.printStackTrace()
    }

  override fun runScope(context: CoroutineContext, block: suspend CoroutineScope.() -> Unit): Unit =
    runBlocking(context, block)

  override fun exit(code: Int): Nothing = exitProcess(code)
}
