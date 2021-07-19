package arrow.fx.coroutines

import arrow.core.Either
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.selects.select
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Races the participants [fa], [fb] in parallel on the [Dispatchers.Default].
 * The winner of the race cancels the other participants.
 * Cancelling the operation cancels all participants.
 * An [uncancellable] participant will back-pressure the result of [raceN].
 *
 * ```kotlin:ank:playground
 * import arrow.core.Either
 * import arrow.fx.coroutines.*
 * import kotlinx.coroutines.suspendCancellableCoroutine
 *
 * suspend fun main(): Unit {
 *   suspend fun loser(): Int =
 *     suspendCancellableCoroutine { cont ->
 *        // Wait forever and never complete callback
 *        cont.invokeOnCancellation { println("Never got cancelled for losing.") }
 *     }
 *
 *   val winner = raceN({ loser() }, { 5 })
 *
 *   val res = when(winner) {
 *     is Either.Left -> "Never always loses race"
 *     is Either.Right -> "Race was won with ${winner.value}"
 *   }
 *   //sampleEnd
 *   println(res)
 * }
 * ```
 *
 * @param fa task to participate in the race
 * @param fb task to participate in the race
 * @return either [Either.Left] if [fa] won the race, or [Either.Right] if [fb] won the race.
 * @see racePair for a version that does not automatically cancel the loser.
 * @see raceN for the same function that can race on any [CoroutineContext].
 */
public suspend inline fun <A, B> raceN(crossinline fa: suspend () -> A, crossinline fb: suspend () -> B): Either<A, B> =
  raceN(Dispatchers.Default, fa, fb)

/**
 * Races the participants [fa], [fb] on the provided [CoroutineContext].
 * The winner of the race cancels the other participants.
 * Cancelling the operation cancels all participants.
 *
 * Coroutine context is inherited from a [CoroutineScope], additional context elements can be specified with [ctx] argument.
 * If the combined context does not have any dispatcher nor any other [ContinuationInterceptor], then [Dispatchers.Default] is used.
 * **WARNING** If the combined context has a single threaded [ContinuationInterceptor], this function will not run [fa] & [fb] in parallel.
 *
 * ```kotlin:ank:playground
 * import arrow.core.Either
 * import arrow.fx.coroutines.*
 * import kotlinx.coroutines.Dispatchers
 * import kotlinx.coroutines.suspendCancellableCoroutine
 *
 * suspend fun main(): Unit {
 *   suspend fun loser(): Int =
 *     suspendCancellableCoroutine { cont ->
 *        // Wait forever and never complete callback
 *        cont.invokeOnCancellation { println("Never got cancelled for losing.") }
 *     }
 *
 *   val winner = raceN(Dispatchers.IO, { loser() }, { 5 })
 *
 *   val res = when(winner) {
 *     is Either.Left -> "Never always loses race"
 *     is Either.Right -> "Race was won with ${winner.value}"
 *   }
 *   //sampleEnd
 *   println(res)
 * }
 * ```
 *
 * @param fa task to participate in the race
 * @param fb task to participate in the race
 * @return either [Either.Left] if [fa] won the race, or [Either.Right] if [fb] won the race.
 * @see raceN for a function that ensures it runs in parallel on the [Dispatchers.Default].
 */
public suspend inline fun <A, B> raceN(
  ctx: CoroutineContext = EmptyCoroutineContext,
  crossinline fa: suspend () -> A,
  crossinline fb: suspend () -> B
): Either<A, B> =
  coroutineScope {
    val a = async(ctx) { fa() }
    val b = async(ctx) { fb() }
    select<Either<A, B>> {
      a.onAwait.invoke { Either.Left(it) }
      b.onAwait.invoke { Either.Right(it) }
    }.also {
      when (it) {
        is Either.Left -> b.cancelAndJoin()
        is Either.Right -> a.cancelAndJoin()
      }
    }
  }
