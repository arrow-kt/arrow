package arrow.fx.coroutines

import arrow.core.raise.catch
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.job
import kotlinx.coroutines.selects.SelectBuilder
import kotlinx.coroutines.selects.select
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@RequiresOptIn(level = RequiresOptIn.Level.WARNING, message = "This API is work-in-progress and is subject to change.")
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
public annotation class ExperimentalRacingApi

/**
 * A scope that allows racing multiple coroutine blocks against each other.
 * The first block to complete successfully will provide the result,
 * and all other blocks will be cancelled.
 *
 * @param Result The type of value that will be returned by the racing blocks.
 */
@ExperimentalRacingApi
public interface RacingScope<in Result> : CoroutineScope {
  /**
   * Races a coroutine block against other blocks in the racing scope.
   * If the block throws an exception
   * or raises an error through [arrow.core.raise.Raise],
   * it will be propagated and cancel the entire racing scope.
   * The block will be cancelled if another block completes first.
   *
   * @param context The [CoroutineContext] to run the block in.
   * @param block The coroutine block to race.
   */
  public fun raceOrFail(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend CoroutineScope.() -> Result
  )
}

private val defaultCoroutineExceptionHandler =
  CoroutineExceptionHandler { _, exception -> exception.printStackTrace() }

/**
 * Races a coroutine block against other blocks in the racing scope.
 * If the block throws an exception
 * or raises an error through [arrow.core.raise.Raise],
 * it will be handled by [CoroutineExceptionHandler].
 * The block will be cancelled if another block completes first.
 *
 * @param context The [CoroutineContext] to run the block in.
 * @param condition Optional condition to consider the block as successful.
 * @param block The coroutine block to race.
 */
@ExperimentalRacingApi
public fun <Result> RacingScope<Result>.race(
  context: CoroutineContext = EmptyCoroutineContext,
  condition: (Result) -> Boolean = { true },
  block: suspend CoroutineScope.() -> Result
): Unit = raceOrFail(context) {
  catch({
    val result = block()
    if (!condition(result)) awaitCancellation()
    result
  }) {
    (coroutineContext[CoroutineExceptionHandler] ?: defaultCoroutineExceptionHandler).handleException(currentCoroutineContext(), it)
    awaitCancellation()
  }
}


/**
 * Creates a racing scope that allows multiple coroutine blocks to race against each other.
 * The first block to complete successfully will provide the result, and all other blocks will be cancelled.
 * A block is by default considered successful if it doesn't throw an exception.
 * To change this behavior, provide a success condition to [race] or use [raceOrFail].
 *
 * This function provides structured concurrency guarantees - all racing blocks will be properly cancelled
 * when the racing scope completes, either normally or exceptionally.
 *
 * Example:
 *
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
 * @param block The block of code to execute within the racing scope.
 * @return The result of the first racing block to complete successfully.
 */
@ExperimentalRacingApi
public suspend fun <A> racing(
  block: RacingScope<A>.() -> Unit,
): A = coroutineScope {
  try {
    select {
      SelectRacingScope(this@select, this@coroutineScope).block()
    }
  } finally {
    coroutineContext.job.cancelChildren()
  }
}

@ExperimentalRacingApi
private class SelectRacingScope<A>(
  private val select: SelectBuilder<A>,
  scope: CoroutineScope,
) : RacingScope<A>, CoroutineScope by scope {
  override fun raceOrFail(context: CoroutineContext, block: suspend CoroutineScope.() -> A) =
    with(select) { async(context, block = block).onAwait { it } }
}
