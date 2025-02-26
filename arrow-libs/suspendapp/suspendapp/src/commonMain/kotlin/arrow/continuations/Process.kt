package arrow.continuations

import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope

/** KMP constructor for [Process]. */
public expect fun process(): Process

/**
 * [Process] offers a common API to work with our application's process, installing signal handlers,
 * shutdown hooks, running scopes in our process (runBlocking), and exiting the process.
 */
@OptIn(ExperimentalStdlibApi::class)
public interface Process : AutoCloseable {
  public fun onSigTerm(block: suspend (code: Int) -> Unit)

  public fun onSigInt(block: suspend (code: Int) -> Unit)

  public fun onShutdown(block: suspend () -> Unit): () -> Unit

  /**
   * On JVM, and Native this will use kotlinx.coroutines.runBlocking, On NodeJS we need an infinite
   * heartbeat to keep main alive. The heartbeat is fast enough that it isn't silently discarded, as
   * longer ticks are, but slow enough that we don't interrupt often.
   * https://stackoverflow.com/questions/23622051/how-to-forcibly-keep-a-node-js-process-from-terminating
   */
  public fun runScope(context: CoroutineContext, callback: (Result<Unit>) -> Unit, block: suspend CoroutineScope.() -> Unit)

  public fun exit(code: Int): Nothing

  override fun close()
}
