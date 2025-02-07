package arrow.continuations

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

public actual fun process(): Process = NativeProcess()

public const val SIGINFO: Int = 29

@OptIn(ExperimentalNativeApi::class)
public val SIGUSR1: Int? =
  when (Platform.osFamily) {
    OsFamily.LINUX -> 10
    OsFamily.MACOSX -> 30
    else -> null
  }

@OptIn(ExperimentalForeignApi::class, ExperimentalStdlibApi::class)
private class NativeProcess : Process, AutoCloseable {
  private val job = SupervisorJob()
  private val scope = CoroutineScope(SIGNAL_DISPATCHER + job)

  override fun onShutdown(block: suspend () -> Unit): suspend () -> Unit {
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

  @OptIn(ExperimentalNativeApi::class, ExperimentalStdlibApi::class)
  override fun close(): Unit = runBlocking {
    // TODO join all jobs, and re-throw all exceptions ??
    //   All jobs should've finished when the Enviroment is closed.
    assert(job.children.none()) { "Job should not have any children anymore." }
    listOf(kotlin.runCatching { job.cancelAndJoin() }, runCatching { SIGNAL_DISPATCHER.close() })
      .getOrThrow()
  }

  override fun runScope(context: CoroutineContext, block: suspend CoroutineScope.() -> Unit) =
    runBlocking(context, block)
}

private fun List<Result<Unit>>.getOrThrow() =
  fold(null) { acc: Throwable?, result ->
    val other = result.exceptionOrNull()
    other?.let { acc?.apply { addSuppressed(other) } ?: other } ?: acc
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
