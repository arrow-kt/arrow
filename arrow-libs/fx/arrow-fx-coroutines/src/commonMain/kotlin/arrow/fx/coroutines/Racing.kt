package arrow.fx.coroutines

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.SelectBuilder
import kotlinx.coroutines.selects.select
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration

/**
 * A scope that allows racing multiple coroutine blocks against each other.
 * The first block to complete successfully will provide the result, and all other blocks will be cancelled.
 *
 * @param Result The type of value that will be returned by the racing blocks.
 */
public interface RacingScope<in Result> : CoroutineScope {
  /**
   * Races a coroutine block against other blocks in the racing scope.
   * If the block throws an exception, it will be handled by the exception handler provided to [racing].
   * The block will be cancelled if another block completes first.
   *
   * @param context The [CoroutineContext] to run the block in.
   * @param block The coroutine block to race.
   */
  public suspend fun race(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend CoroutineScope.() -> Result
  )

  /**
   * Races a coroutine block against other blocks in the racing scope.
   * If the block throws an exception, it will be propagated and cancel the entire racing scope.
   * The block will be cancelled if another block completes first.
   *
   * @param context The [CoroutineContext] to run the block in.
   * @param block The coroutine block to race.
   */
  public suspend fun raceOrThrow(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend CoroutineScope.() -> Result
  )
}

/**
 * Creates a racing scope that allows multiple coroutine blocks to race against each other.
 * The first block to complete successfully will provide the result, and all other blocks will be cancelled.
 *
 * This function provides structured concurrency guarantees - all racing blocks will be properly cancelled
 * when the racing scope completes, either normally or exceptionally.
 *
 * Example:
 * ```kotlin
 * import arrow.fx.coroutines.racing
 * import kotlinx.coroutines.delay
 * import kotlin.time.Duration.Companion.milliseconds
 *
 * suspend fun main() {
 *   val result = racing<String> {
 *     // This block will be cancelled when the timeout completes
 *     race {
 *       delay(1000)
 *       "Slow result"
 *     }
 *
 *     // This block will complete first and provide the result
 *     onTimeout(time = 100.milliseconds) {
 *       "Timeout result"
 *     }
 *   }
 *
 *   println(result) // Prints: Timeout result
 * }
 * ```
 *
 * @param A The type of value that will be returned by the racing blocks.
 * @param handleException A function to handle exceptions thrown by racing blocks. If null, exceptions will be handled
 *                       by the current coroutine context's [CoroutineExceptionHandler] or printed to stderr.
 * @param block The block of code to execute within the racing scope.
 * @return The result of the first racing block to complete successfully.
 */
public suspend fun <A> racing(
  handleException: (suspend (context: CoroutineContext, exception: Throwable) -> Unit)? = null,
  block: suspend RacingScope<A>.() -> Unit,
): A {
  val exceptionHandler: suspend (context: CoroutineContext, exception: Throwable) -> Unit =
    handleException
      ?: currentCoroutineContext()[CoroutineExceptionHandler]?.let { { ctx, e -> it.handleException(ctx, e) } }
      ?: { _, e -> e.printStackTrace() }
  return coroutineScope {
    val job = Job(currentCoroutineContext()[Job])
    try {
      select {
        launch(start = CoroutineStart.UNDISPATCHED) {
          val scope = SelectRacingScope(this@select, this@coroutineScope, job, exceptionHandler)
          scope.block()
        }
      }
    } finally {
      job.cancelAndJoin()
    }
  }
}

private class SelectRacingScope<A>(
  private val select: SelectBuilder<A>,
  scope: CoroutineScope,
  private val job: Job,
  private val handleException: suspend (context: CoroutineContext, exception: Throwable) -> Unit
) : RacingScope<A>, CoroutineScope by scope {
  override suspend fun raceOrThrow(context: CoroutineContext, block: suspend CoroutineScope.() -> A) =
    with(select) {
      async(context = context + job, block = block).onAwait { it }
    }

  override suspend fun race(context: CoroutineContext, block: suspend CoroutineScope.() -> A) =
    raceOrThrow {
      try {
        block()
      } catch (e: CancellationException) {
        throw e
      } catch (e: Throwable) {
        handleException(currentCoroutineContext(), e/*.nonFatalOrThrow()*/)
        awaitCancellation()
      }
    }
}
