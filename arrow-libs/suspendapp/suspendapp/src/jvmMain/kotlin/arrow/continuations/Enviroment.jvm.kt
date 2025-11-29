package arrow.continuations

import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.CoroutineContext
import kotlin.system.exitProcess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import sun.misc.Signal
import sun.misc.SignalHandler

internal actual fun process(): Process = JvmProcess

private object JvmProcess : Process {
  override fun onShutdown(block: suspend () -> Unit): () -> Unit {
    val isShutdown = AtomicBoolean(false)
    fun shutdown() {
      if (!isShutdown.getAndSet(true))
        runBlocking {
          // We don't call exit from ShutdownHook on JVM
          try {
            block()
          } catch (e: Throwable) {
            e.printStackTrace()
          }
        }
    }

    val hook = Thread(::shutdown, "Arrow-kt SuspendApp JVM ShutdownHook")
    Runtime.getRuntime().addShutdownHook(hook)
    return {
      if (!isShutdown.get()) {
        Runtime.getRuntime().removeShutdownHook(hook)
      }
    }
  }

  override fun onSigTerm(block: suspend (code: Int) -> Unit): Unit =
    addSignalHandler("SIGTERM") { block(15) }

  override fun onSigInt(block: suspend (code: Int) -> Unit): Unit =
    addSignalHandler("SIGINT") { block(2) }

  private fun addSignalHandler(signal: String, action: suspend () -> Unit): Unit =
    try {
      var handle: SignalHandler? = null
      @Suppress("ASSIGNED_VALUE_IS_NEVER_READ")
      handle =
        Signal.handle(Signal(signal)) { prev ->
          runBlocking { action() }
          if (handle != SignalHandler.SIG_DFL && handle != SignalHandler.SIG_IGN) {
            handle?.handle(prev)
          }
        }
    } catch (_: Throwable) {
      /* Ignore */
    }

  override fun runScope(context: CoroutineContext, block: suspend CoroutineScope.() -> Unit): Unit =
    runBlocking(context, block)

  override fun exit(code: Int): Nothing = exitProcess(code)

  override fun close(): Unit = Unit
}
