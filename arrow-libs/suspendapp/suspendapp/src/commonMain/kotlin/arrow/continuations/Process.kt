package arrow.continuations

import arrow.AutoCloseScope
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope

/** KMP constructor for [Process]. */
internal expect fun AutoCloseScope.process(): Process

/**
 * [Process] offers a common API to work with our application's process, installing signal handlers,
 * shutdown hooks, running scopes in our process (runBlocking), and exiting the process.
 */
@OptIn(ExperimentalStdlibApi::class)
internal interface Process {
  fun onSigTerm(block: suspend (code: Int) -> Unit)

  fun onSigInt(block: suspend (code: Int) -> Unit)

  /**
   * Registers [block] to run when the process is asked to shut down. [block] receives the intended [exitCode] (usually the conventional exit
   * status for the signal, i.e. `128 + signal number`).
   *
   * [block] is not required to exit the process before completion and may defer or delegate process exit.
   * Platforms that can exit the process only do so here if [block] fails (e.g. the shutdown timeout expires).
   */
  fun onShutdown(block: suspend (exitCode: Int) -> Unit): () -> Unit

  /**
   * On JVM, and Native this will use kotlinx.coroutines.runBlocking, On NodeJS we need an infinite
   * heartbeat to keep main alive. The heartbeat is fast enough that it isn't silently discarded, as
   * longer ticks are, but slow enough that we don't interrupt often.
   * https://stackoverflow.com/questions/23622051/how-to-forcibly-keep-a-node-js-process-from-terminating
   */
  fun runScope(context: CoroutineContext, block: suspend CoroutineScope.() -> Unit)

  fun exit(code: Int): Nothing
}

/**
 * Runs the graceful-shutdown [block], and forcefully exits if it fails (e.g. the SuspendApp timeout expires).
 *
 * On success this deliberately does _not_ exit: the application's main flow observes the cancellation and is the
 * single place that exits, with the exit code carried by the shutdown. If the timeout expires before the main flow
 * reaches that exit, the timeout wins and the process exits with -1.
 *
 * Used by the platforms that signal from a handler and are able to exit from it; the JVM shutdown hook must not exit.
 */
internal inline fun Process.exitOnFailure(block: () -> Unit): Unit =
  try {
    block()
  } catch (e: Throwable) {
    e.printStackTrace()
    exit(-1)
  }
