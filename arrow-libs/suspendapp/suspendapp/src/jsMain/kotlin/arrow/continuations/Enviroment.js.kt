package arrow.continuations

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine
import kotlin.js.Promise
import kotlin.time.Duration.Companion.hours
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.promise
import kotlinx.coroutines.withContext

internal actual fun process(): Process = JsProcess

private object JsProcess : Process {
  override fun onShutdown(block: suspend () -> Unit): () -> Unit {
    onSigTerm { code -> exitAfter(128 + code) { block() } }
    onSigInt { code -> exitAfter(128 + code) { block() } }
    return { /* Nothing to unregister */ }
  }

  override fun onSigTerm(block: suspend (code: Int) -> Unit): Unit = onSignal("SIGTERM") { block(15) }

  override fun onSigInt(block: suspend (code: Int) -> Unit): Unit = onSignal("SIGINT") { block(2) }

  @OptIn(DelicateCoroutinesApi::class)
  @Suppress("UNUSED_PARAMETER")
  private fun onSignal(signal: String, block: suspend () -> Unit) {
    @Suppress("UNUSED_VARIABLE")
    val provide: () -> Promise<Unit> = { GlobalScope.promise { block() } }
    js("process.on(signal, function() {\n" + "  provide()\n" + "});")
  }

  override fun runScope(context: CoroutineContext, block: suspend CoroutineScope.() -> Unit) {
    val innerJob = Job()
    val innerScope = CoroutineScope(innerJob)
    suspend {
        // An infinite heartbeat to keep main alive.
        // The tick is fast enough that  it isn't silently discarded, as longer ticks are,
        // but slow enough that we don't interrupt often.
        // https://stackoverflow.com/questions/23622051/how-to-forcibly-keep-a-node-js-process-from-terminating
        val keepAlive: Job =
          innerScope.launch {
            while (isActive) {
              // Schedule a `no-op tick` by returning to main every hour with no actual work but
              // just looping.
              delay(1.hours)
            }
          }
        runCatching { withContext(context, block) }
          .also { keepAlive.cancelAndJoin() }
          .getOrThrow()
    }
      .startCoroutine(Continuation(EmptyCoroutineContext) {})
  }

  override fun exit(code: Int): Nothing {
    js("process.exit(code)")
    error("process.exit() should have exited...")
  }

  override fun close(): Unit = Unit
}

private inline fun Process.exitAfter(code: Int, block: () -> Unit): Unit =
  try {
    block()
    exit(code)
  } catch (e: Throwable) {
    e.printStackTrace()
    exit(-1)
  }
