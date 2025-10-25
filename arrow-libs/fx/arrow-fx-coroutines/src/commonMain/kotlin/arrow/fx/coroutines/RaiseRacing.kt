package arrow.fx.coroutines

import arrow.core.raise.Raise
import arrow.core.raise.recover
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.currentCoroutineContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * A scope that combines [CoroutineScope] and [Raise] capabilities, allowing for both coroutine operations
 * and error handling within a single scope.
 *
 * @param Error The type of error that can be raised within this scope.
 */
public interface RaiseScope<in Error> : CoroutineScope, Raise<Error>

private class RaiseScopeImpl<Error>(scope: CoroutineScope, raise: Raise<Error>) : RaiseScope<Error>, CoroutineScope by scope, Raise<Error> by raise

public typealias RaiseHandler<Error> = (context: CoroutineContext, error: Error) -> Unit

/**
 * Races a coroutine block against other blocks in the racing scope.
 * If the block raises an error, it will be handled by [handleError].
 * The block will be cancelled if another block completes first.
 *
 * @param context The [CoroutineContext] to run the block in.
 * @param block The coroutine block to race.
 */
@ExperimentalRacingApi
public fun <Error, Result> RacingScope<Result>.raceOrFail(
  handleError: RaiseHandler<Error>,
  context: CoroutineContext = EmptyCoroutineContext,
  block: suspend RaiseScope<Error>.() -> Result,
): Unit = raceOrFail(context) scope@{
  recover({ block(RaiseScopeImpl(this@scope, this)) }) {
    handleError(currentCoroutineContext(), it)
    awaitCancellation()
  }
}

/**
 * Races a coroutine block against other blocks in the racing scope.
 * If the block raises an error, it will be handled by [handleError].
 * If the block throws an exception, it will be handled by [kotlinx.coroutines.CoroutineExceptionHandler].
 * The block will be cancelled if another block completes first.
 *
 * @param context The [CoroutineContext] to run the block in.
 * @param block The coroutine block to race.
 */
@ExperimentalRacingApi
public fun <Error, Result> RacingScope<Result>.race(
  handleError: RaiseHandler<Error>,
  context: CoroutineContext = EmptyCoroutineContext,
  block: suspend RaiseScope<Error>.() -> Result,
): Unit = race(context) scope@{
  recover({ block(RaiseScopeImpl(this@scope, this)) }) {
    handleError(currentCoroutineContext(), it)
    awaitCancellation()
  }
}

/**
 * A scope that combines [RacingScope] and [Raise] capabilities,
 * allowing for both racing operations and error handling within a single scope.
 *
 * @param Error The type of error that can be raised within this scope.
 * @param Result The type of value that will be returned by the racing blocks.
 */
@ExperimentalRacingApi
public interface RacingRaise<in Error, in Result> : Raise<Error>, RacingScope<Result>

@ExperimentalRacingApi
private class RacingRaiseImpl<in Error, in Result>(
  private val racing: RacingScope<Result>,
  private val outerRaise: Raise<Error>
) : RacingRaise<Error, Result>, Raise<Error>, RacingScope<Result> by racing {
  override fun raise(r: Error): Nothing = throw RacingRaiseException(r, outerRaise)
}

/**
 * Creates a racing scope that allows multiple coroutine blocks to race against each other.
 * The first block to complete successfully will provide the result, and all other blocks will be cancelled.
 * A block is by default considered successful if it doesn't throw an exception
 * nor raises an error through [arrow.core.raise.Raise].
 * To change this behavior, provide a success condition to [race] or use [raceOrFail].
 *
 * This function provides structured concurrency guarantees - all racing blocks will be properly cancelled
 * when the racing scope completes, either normally or exceptionally.
 *
 * Example 1:
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
 * Example 2:
 *
 * ```kotlin
 * import arrow.fx.coroutines.racing
 * import arrow.core.raise.either
 *
 * suspend fun main() {
 *   val result = either<String, Int> {
 *     racing<String> {
 *       race { delay(1000) ; 104 }
 *
 *       // this blocks ends earlier,
 *       // but it is not considered successful
 *       race { raise("a problem") }
 *     }
 *   }
 *
 *   println(result) // Prints: Either.Right(104)
 * }
 * ```
 *
 * @param A The type of value that will be returned by the racing blocks.
 * @param raiseBlock The block of code to execute within the racing scope.
 * @return The result of the first racing block to complete successfully.
 */
@ExperimentalRacingApi
public suspend fun <Error, A> Raise<Error>.racing(
  raiseBlock: RacingRaise<Error, A>.() -> Unit,
): A {
  val outerRaise = this
  try {
    return racing(block = racingScope@{ raiseBlock(RacingRaiseImpl(this@racingScope, outerRaise)) })
  } catch (e: RacingRaiseException) {
    @Suppress("UNCHECKED_CAST")
    when {
      e.outerRaise === outerRaise -> raise(e.error as Error)
      else -> throw e
    }
  }
}

/**
 * Internal exception type for [racing] implementation.
 *
 * Important: do **not** implement [kotlinx.coroutines.CancellationException],
 * otherwise calling `raise` cancels the entire block right away.
 */
@ExperimentalRacingApi
internal class RacingRaiseException(val error: Any?, val outerRaise: Raise<*>) : Exception("Raise: $error")
