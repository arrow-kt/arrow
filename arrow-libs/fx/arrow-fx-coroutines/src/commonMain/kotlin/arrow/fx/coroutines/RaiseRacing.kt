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
