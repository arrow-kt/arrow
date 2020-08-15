package arrow.fx.coroutines

import arrow.core.Either
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.intrinsics.intercepted
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED

/**
 * Races the participants [fa], [fb] in parallel on the [ComputationPool].
 * The winner of the race cancels the other participants,
 * cancelling the operation cancels all participants.
 * An [uncancellable] participants will back-pressure the result of [raceN].
 *
 * ```kotlin:ank:playground
 * import arrow.core.Either
 * import arrow.fx.coroutines.*
 *
 * suspend fun main(): Unit {
 *   suspend fun loser(): Int =
 *     cancellable { callback ->
 *        // Wait forever and never complete callback
 *        CancelToken { println("Never got cancelled for losing.") }
 *     }
 *
 *   val winner = raceN({ loser() }, { 5 })
 *
 *   val res = when(winner) {
 *     is Either.Left -> "Never always loses race"
 *     is Either.Right -> "Race was won with ${winner.b}"
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
suspend fun <A, B> raceN(fa: suspend () -> A, fb: suspend () -> B): Either<A, B> =
  raceN(ComputationPool, fa, fb)

/**
 * Races the participants [fa], [fb] on the provided [CoroutineContext].
 * The winner of the race cancels the other participants,
 * cancelling the operation cancels all participants.
 *
 * **WARNING** it runs in parallel depending on the capabilities of the provided [CoroutineContext].
 * We ensure they start in sequence so it's guaranteed to finish on a single threaded context.
 *
 * ```kotlin:ank:playground
 * import arrow.core.Either
 * import arrow.fx.coroutines.*
 *
 * suspend fun main(): Unit {
 *   suspend fun loser(): Int =
 *     cancellable { callback ->
 *        // Wait forever and never complete callback
 *        CancelToken { println("Never got cancelled for losing.") }
 *     }
 *
 *   val winner = raceN(IOPool, { loser() }, { 5 })
 *
 *   val res = when(winner) {
 *     is Either.Left -> "Never always loses race"
 *     is Either.Right -> "Race was won with ${winner.b}"
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
 * @see raceN for a function that ensures it runs in parallel on the [ComputationPool].
 */
suspend fun <A, B> raceN(ctx: CoroutineContext, fa: suspend () -> A, fb: suspend () -> B): Either<A, B> {
  fun <T, U> onSuccess(
    isActive: AtomicBooleanW,
    main: SuspendConnection,
    other: SuspendConnection,
    cb: (Result<Either<T, U>>) -> Unit,
    r: Either<T, U>
  ): Unit =
    if (isActive.getAndSet(false)) {
      other.cancelToken().cancel.startCoroutine(Continuation(EmptyCoroutineContext) { r2 ->
        main.pop()
        r2.fold({
          cb(Result.success(r))
        }, { e ->
          cb(Result.failure(e))
        })
      })
    } else Unit

  fun onError(
    active: AtomicBooleanW,
    cb: (Result<Nothing>) -> Unit,
    main: SuspendConnection,
    other: SuspendConnection,
    err: Throwable
  ): Unit =
    if (active.getAndSet(false)) {
      other.cancelToken().cancel.startCoroutine(Continuation(ComputationPool) { r2: Result<Unit> ->
        main.pop()
        cb(Result.failure(r2.fold({ err }, { Platform.composeErrors(err, it) })))
      })
    } else Unit

  return suspendCoroutineUninterceptedOrReturn { cont ->
    val conn = cont.context.connection()
    val cont = cont.intercepted()

    val active = AtomicBooleanW(true)
    val connA = SuspendConnection()
    val connB = SuspendConnection()
    conn.pushPair(connA, connB)

    fa.startCoroutineCancellable(CancellableContinuation(ctx, connA) { result ->
      result.fold({
        onSuccess(active, conn, connB, cont::resumeWith, Either.Left(it))
      }, {
        onError(active, cont::resumeWith, conn, connB, it)
      })
    })

    fb.startCoroutineCancellable(CancellableContinuation(ctx, connB) { result ->
      result.fold({
        onSuccess(active, conn, connA, cont::resumeWith, Either.Right(it))
      }, {
        onError(active, cont::resumeWith, conn, connA, it)
      })
    })

    COROUTINE_SUSPENDED
  }
}
