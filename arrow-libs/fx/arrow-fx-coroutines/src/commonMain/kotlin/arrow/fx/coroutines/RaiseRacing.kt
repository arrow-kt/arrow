package arrow.fx.coroutines

import arrow.core.raise.Raise
import arrow.core.raise.recover
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
import kotlin.time.Duration

/**
 * A scope that combines [CoroutineScope] and [Raise] capabilities, allowing for both coroutine operations
 * and error handling within a single scope.
 *
 * @param Error The type of error that can be raised within this scope.
 */
public interface RaiseScope<in Error> : CoroutineScope, Raise<Error>

/**
 * A scope that allows racing multiple coroutine blocks against each other with [Raise] context receiver support.
 * The first block to complete successfully will provide the result, and all other blocks will be cancelled.
 *
 * @param Error The error type that can be raised by the racing blocks.
 * @param Result The type of value that will be returned by the racing blocks.
 */
public interface RaiseRacingScope<in Error, in Result> {
  /**
   * Races a coroutine block against other blocks in the racing scope.
   * If the block throws an exception, it will be handled by the exception handler provided to [racing].
   * If the block raises an error, it will be handled by the raise handler provided to [racing].
   * The block will be cancelled if another block completes first.
   *
   * @param context The [CoroutineContext] to run the block in.
   * @param block The coroutine block to race with [Raise] and [CoroutineScope].
   */
  public suspend fun race(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend RaiseScope<Error>.() -> Result
  )

  /**
   * Races a coroutine block against other blocks in the racing scope.
   * If the block throws an exception, it will be handled by the exception handler provided to [racing].
   * If the block raises an error, it will be propagated and cancel the entire racing scope.
   * The block will be cancelled if another block completes first.
   *
   * @param context The [CoroutineContext] to run the block in.
   * @param block The coroutine block to race with [Raise] and [CoroutineScope].
   */
  public suspend fun raceOrRaise(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend RaiseScope<Error>.() -> Result
  )

  /**
   * Races a coroutine block against other blocks in the racing scope.
   * If the block throws an exception, it will be propagated and cancel the entire racing scope.
   * If the block raises an error, it will be handled by the raise handler provided to [racing].
   * The block will be cancelled if another block completes first.
   *
   * @param context The [CoroutineContext] to run the block in.
   * @param block The coroutine block to race with [Raise] and [CoroutineScope].
   */
  public suspend fun raceOrThrow(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend RaiseScope<Error>.() -> Result
  )

  /**
   * Races a coroutine block against other blocks in the racing scope.
   * If the block throws an exception, it will be propagated and cancel the entire racing scope.
   * If the block raises an error, it will be propagated and cancel the entire racing scope.
   * The block will be cancelled if another block completes first.
   *
   * @param context The [CoroutineContext] to run the block in.
   * @param block The coroutine block to race with [Raise] and [CoroutineScope].
   */
  public suspend fun raceOrFail(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend RaiseScope<Error>.() -> Result
  )
}

/**
 * Creates a racing scope that allows multiple coroutine blocks with [Raise] context receiver to race against each other.
 * The first block to complete successfully will provide the result, and all other blocks will be cancelled.
 *
 * This function extends the functionality of [racing] by adding support for Arrow's [Raise] monad,
 * allowing for more expressive error handling within racing blocks.
 *
 * This function provides structured concurrency guarantees - all racing blocks will be properly cancelled
 * when the racing scope completes, either normally or exceptionally.
 *
 * @param E The error type that can be raised by the racing blocks.
 * @param A The type of value that will be returned by the racing blocks.
 * @param handleRaise A function to handle unhandled errors raised by racing blocks. If null, unhandled raised errors will be ignored.
 * @param handleException A function to handle unhandled exceptions thrown by racing blocks. If null, exceptions will be handled
 *                       by the current coroutine context's [CoroutineExceptionHandler] or printed to stderr.
 * @param block The block of code to execute within the racing scope.
 * @return The result of the first racing block to complete successfully.
 */
public suspend fun <E, A> Raise<E>.racing(
  handleRaise: (suspend (context: CoroutineContext, raised: E) -> Unit)? = null,
  handleException: (suspend (context: CoroutineContext, exception: Throwable) -> Unit)? = null,
  block: suspend RaiseRacingScope<E, A>.() -> Unit,
): A {
  val handleRaise: suspend (context: CoroutineContext, raised: E) -> Unit =
    handleRaise ?: { _, _ -> }
  val exceptionHandler: suspend (context: CoroutineContext, exception: Throwable) -> Unit =
    handleException
      ?: currentCoroutineContext()[CoroutineExceptionHandler]?.let { { ctx, e -> it.handleException(ctx, e) } }
      ?: { _, e -> e.printStackTrace() }
  return coroutineScope {
    val job = Job(currentCoroutineContext()[Job])
    recover({
      try {
        select {
          launch(start = CoroutineStart.UNDISPATCHED) {
            val scope = SelectRaiseRacingScope(
              this@select,
              this@coroutineScope,
              this@recover,
              job,
              handleRaise,
              exceptionHandler
            )
            scope.block()
          }
        }
      } finally {
        job.cancelAndJoin()
      }
    }) { e ->
      raise(e)
    }
  }
}

private class SelectRaiseRacingScope<E, A>(
  private val select: SelectBuilder<A>,
  scope: CoroutineScope,
  private val raise: Raise<E>,
  private val job: Job,
  private val raiseHandler: suspend (CoroutineContext, E) -> Unit,
  private val exceptionHandler: suspend (CoroutineContext, Throwable) -> Unit,
) : RaiseScope<E>, Raise<E> by raise, RaiseRacingScope<E, A>, CoroutineScope by scope {
  override suspend fun race(
    context: CoroutineContext,
    block: suspend RaiseScope<E>.() -> A
  ) = raceImpl(failOnThrow = false, failOnRaise = false, context, block)

  override suspend fun raceOrRaise(
    context: CoroutineContext,
    block: suspend RaiseScope<E>.() -> A
  ) = raceImpl(failOnThrow = false, failOnRaise = true, context, block)

  override suspend fun raceOrThrow(
    context: CoroutineContext,
    block: suspend RaiseScope<E>.() -> A
  ) = raceImpl(failOnThrow = true, failOnRaise = false, context, block)

  override suspend fun raceOrFail(
    context: CoroutineContext,
    block: suspend RaiseScope<E>.() -> A
  ) = raceImpl(failOnThrow = true, failOnRaise = true, context, block)

  private fun raceImpl(
    failOnThrow: Boolean,
    failOnRaise: Boolean,
    context: CoroutineContext,
    block: suspend RaiseScope<E>.() -> A
  ) = with(select) {
    async(context = context + job) {
      recover({ block() }, { error: E ->
        if (failOnRaise) {
          raise.raise(error)
        } else {
          raiseHandler(this@async.coroutineContext, error)
          awaitCancellation()
        }
      }, { throwable ->
        if (failOnThrow) {
          throw throwable
        } else {
          exceptionHandler(this@async.coroutineContext, throwable)
          awaitCancellation()
        }
      })
    }.onAwait { it }
  }
}
