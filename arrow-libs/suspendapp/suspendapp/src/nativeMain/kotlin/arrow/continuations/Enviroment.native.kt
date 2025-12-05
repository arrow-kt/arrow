package arrow.continuations

import arrow.AutoCloseScope
import kotlin.coroutines.CoroutineContext
import kotlin.experimental.ExperimentalNativeApi
import kotlin.system.exitProcess
import kotlinx.cinterop.CFunction
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.staticCFunction
import kotlinx.coroutines.*
import platform.posix.SIGINT
import platform.posix.SIGTERM
import platform.posix.signal

@OptIn(DelicateCoroutinesApi::class, ExperimentalNativeApi::class)
internal actual fun AutoCloseScope.process(): Process {
  install(SIGNAL_DISPATCHER)
  val job = SupervisorJob()
  onClose {
    // TODO join all jobs, and re-throw all exceptions ??
    //   All jobs should've finished when the Enviroment is closed.
    assert(job.children.none()) { "Job should not have any children anymore." }
    runBlocking { job.cancelAndJoin() }
  }
  return NativeProcess(job)
}

@OptIn(ExperimentalForeignApi::class, ExperimentalStdlibApi::class)
private class NativeProcess(job: Job) : Process {
  private val scope = CoroutineScope(SIGNAL_DISPATCHER + job)

  override fun onShutdown(block: suspend () -> Unit): () -> Unit {
    onSigTerm { exitAfter(it + 128) { block() } }
    onSigInt { exitAfter(it + 128) { block() } }
    return { /* Nothing to unregister */ }
  }

  override fun exit(code: Int): Nothing = exitProcess(code)

  override fun onSigTerm(block: suspend (code: Int) -> Unit) =
    onSignal(SIGTERM, SIGTERM_HANDLER, TERMINATED, block)

  override fun onSigInt(block: suspend (code: Int) -> Unit) =
    onSignal(SIGINT, SIGINT_HANDLER, INTERRUPTED, block)

  private fun onSignal(
    code: Int,
    handler: CPointer<CFunction<(Int) -> Unit>>,
    signal: CompletableDeferred<Int>,
    block: suspend (code: Int) -> Unit,
  ) {
    scope.launch {
      val signalCode = signal.await()
      block(signalCode)
    }
    signal(code, handler)
  }

  override fun runScope(context: CoroutineContext, block: suspend CoroutineScope.() -> Unit): Unit =
    runBlocking(context, block)
}

private val TERMINATED: CompletableDeferred<Int> = CompletableDeferred()

private val INTERRUPTED: CompletableDeferred<Int> = CompletableDeferred()

@OptIn(ExperimentalForeignApi::class)
private val SIGTERM_HANDLER =
  staticCFunction<Int, Unit> { code ->
    TERMINATED.complete(code)
    runBlocking(SIGNAL_DISPATCHER) { BACKPRESSURE.await() }
  }

@OptIn(ExperimentalForeignApi::class)
private val SIGINT_HANDLER =
  staticCFunction<Int, Unit> { code ->
    INTERRUPTED.complete(code)
    runBlocking(SIGNAL_DISPATCHER) { BACKPRESSURE.await() }
  }

private val BACKPRESSURE: CompletableDeferred<Int> = CompletableDeferred()

@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
private val SIGNAL_DISPATCHER: CloseableCoroutineDispatcher =
  newSingleThreadContext("arrow-kt-suspendapp-signal-dispatcher")

private inline fun Process.exitAfter(code: Int, block: () -> Unit): Unit =
  try {
    block()
    exit(code)
  } catch (e: Throwable) {
    e.printStackTrace()
    exit(-1)
  }
