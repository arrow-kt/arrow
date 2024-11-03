package arrow.fx.coroutines

import arrow.core.identity
import arrow.core.nonFatalOrThrow
import arrow.core.raise.Raise
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import kotlinx.coroutines.selects.SelectBuilder
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.cancellation.CancellationException

public interface RaiseRacingScope<Error, A> : Raise<Error> {
  public suspend fun race(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend Raise<Error>.() -> A
  )

  public suspend fun raceOrRaise(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend Raise<Error>.() -> A
  )

  public suspend fun raceOrThrow(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend Raise<Error>.() -> A
  )

  public suspend fun raceOrFail(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend Raise<Error>.() -> A
  )
}

/*public suspend fun <Error, A> Raise<Error>.racing(
  dropped: suspend (Error) -> Unit,
  handleException: (suspend (context: CoroutineContext, exception: Throwable) -> Unit)? = null,
  block: RacingScope<A>.() -> Unit,
): A {
  TODO()
}*/

/**
 * A DSL that allows racing many `suspend` functions in parallel against each-other,
 * it yields a final result of [A] based on the first function that yields a result.
 * A racer can yield a result based on [race], or [raceOrFail].
 *
 * [race] will call the current [CoroutineExceptionHandler] in case of an exception,
 * and then await **another successful result** but not cancel the race. Whilst [raceOrFail] will cancel the race,
 * and rethrow the exception that occurred and thus cancel the race and all participating racers.
 *
 * <!--- INCLUDE
 * import arrow.fx.coroutines.race
 * import arrow.fx.coroutines.raceOrFail
 * import kotlinx.coroutines.coroutineScope
 * import kotlinx.coroutines.delay
 * import kotlinx.coroutines.selects.select
 * -->
 * ```kotlin
 * suspend fun winner(): String = coroutineScope {
 *   select {
 *     race { delay(1000); "Winner" }
 *     race { throw RuntimeException("Loser") }
 *   }
 * } // Winner (logged RuntimeException)
 *
 * suspend fun winner2(): String = coroutineScope {
 *   select {
 *     race { delay(1000); "Winner" }
 *     raceOrFail { throw RuntimeException("Loser") }
 *   }
 * } // RuntimeException
 * ```
 *
 * **Important:** a racing program with no racers will hang forever.
 * ```kotlin
 * suspend fun never(): Nothing = select { }
 * ```
 * <!--- KNIT example-racing-01.kt -->
 *
 * @param block the body of the DSL that describes the racing logic
 * @return the winning value of [A].
 */
public val <A> SelectBuilder<A>.raceOrFail: CoroutineScope.(suspend CoroutineScope.() -> A) -> Unit get () = { block ->
  /* First we create a lazy racer,
   * and we add it in front of the existing racers such that we maintain correct order.
   * After we've successfully registered the racer, we check for race conditions,
   * and 'start' racing.
   */
  val racer = async(
    start = CoroutineStart.LAZY,
    block = block
  )
  if (isActive) {
    require(racer.start()) { "Racer not started" }
    racer.onAwait(::identity)
  }
}

public val <A> SelectBuilder<A>.race: CoroutineScope.(suspend CoroutineScope.() -> A) -> Unit get() = {  block ->
  raceOrFail {
    try {
      block()
    } catch (e: Throwable) {
      currentExceptionHandler().handleException(currentCoroutineContext(), e.nonFatalOrThrow())
      awaitCancellation()
    }
  }
}

private suspend fun currentExceptionHandler(): CoroutineExceptionHandler =
  currentCoroutineContext()[CoroutineExceptionHandler] ?: DefaultCoroutineExceptionHandler

private object DefaultCoroutineExceptionHandler : CoroutineExceptionHandler {
  override val key: CoroutineContext.Key<CoroutineExceptionHandler> = CoroutineExceptionHandler

  override fun handleException(context: CoroutineContext, exception: Throwable) {
    if (exception !is CancellationException) exception.printStackTrace()
  }
}
