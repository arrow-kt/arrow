package arrow.fx.coroutines

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.intrinsics.intercepted

sealed class RaceTriple<A, B, C> {
  data class First<A, B, C>(val winner: A, val fiberB: Fiber<B>, val fiberC: Fiber<C>) : RaceTriple<A, B, C>()
  data class Second<A, B, C>(val fiberA: Fiber<A>, val winner: B, val fiberC: Fiber<C>) : RaceTriple<A, B, C>()
  data class Third<A, B, C>(val fiberA: Fiber<A>, val fiberB: Fiber<B>, val winner: C) : RaceTriple<A, B, C>()

  inline fun <D> fold(
    ifA: (A, Fiber<B>, Fiber<C>) -> D,
    ifB: (Fiber<A>, B, Fiber<C>) -> D,
    ifC: (Fiber<A>, Fiber<B>, C) -> D
  ): D = when (this) {
    is First -> ifA(winner, fiberB, fiberC)
    is Second -> ifB(fiberA, winner, fiberC)
    is Third -> ifC(fiberA, fiberB, winner)
  }
}

suspend fun <A, B, C> raceTriple(fa: suspend () -> A, fb: suspend () -> B, fc: suspend () -> C): RaceTriple<A, B, C> =
  raceTriple(Dispatchers.Default, fa, fb, fc)

/**
 * Races three tasks concurrently within a new suspend fun.
 * Race results in a winner and the others, yet to finish tasks running in [Fiber].
 *
 * ```kotlin:ank:playground
 * import arrow.fx.coroutines.*
 *
 * suspend fun main(): Unit {
 *   //sampleStart
 *   val res = raceTriple({ never<Int>() }, { never<Int>() }, { "Hello World!" })
 *   val r = when(res) {
 *     is RaceTriple.First -> "never cannot win race"
 *     is RaceTriple.Second -> "never cannot win race"
 *     is RaceTriple.Third -> res.winner
 *   }
 *   //sampleEnd
 *   println("Race winner result is: $r")
 * }
 * ```
 *
 * @param ctx [CoroutineContext] to execute the source [fa], [fb] & [fc] on.
 * @param fa task to participate in the race
 * @param fb task to participate in the race
 * @param fc task to participate in the race
 * @return either [RaceTriple.First] with product of the winner's result [ƒa] and still running tasks [fb] & [fc],
 *   or [RaceTriple.Second] with product of running tasks [ƒa] & [fc]  and the winner's result [fb],
 *   or [RaceTriple.Third] with product of running tasks [ƒa] & [fb]  and the winner's result [fc].
 *
 * @see [arrow.fx.coroutines.raceN] for a simpler version that cancels loser.
 */
@Deprecated("Will be removed since it leaks Fiber, and breaks structured concurrency. Replace with select")
suspend fun <A, B, C> raceTriple(
  ctx: CoroutineContext,
  fa: suspend () -> A,
  fb: suspend () -> B,
  fc: suspend () -> C
): RaceTriple<A, B, C> =
  suspendCancellableCoroutine { cont ->
    if (cont.isActive) {
      val disposable = suspend {
        oldRaceTriple(ctx, fa, fb, fc)
      }.startCoroutineCancellable(CancellableContinuation(cont.context, cont::resumeWith))
      cont.invokeOnCancellation { disposable() }
    }
  }

suspend fun <A, B, C> oldRaceTriple(
  ctx: CoroutineContext,
  fa: suspend () -> A,
  fb: suspend () -> B,
  fc: suspend () -> C
): RaceTriple<A, B, C> =
  suspendCoroutineUninterceptedOrReturn { cont ->
    val conn = cont.context[SuspendConnection] ?: SuspendConnection.uncancellable
    val cont = cont.intercepted()
    val active = AtomicBooleanW(true)

    // Cancellable connection for the left value
    val jobA = Job()
    val connA = SuspendConnection()
    connA.push { jobA.cancelAndJoin() }
    val promiseA = UnsafePromise<A>()

    // Cancellable connection for the right value
    val jobB = Job()
    val connB = SuspendConnection()
    connB.push { jobB.cancelAndJoin() }
    val promiseB = UnsafePromise<B>()

    // Cancellable connection for the right value
    val jobC = Job()
    val connC = SuspendConnection()
    connC.push { jobC.cancelAndJoin() }
    val promiseC = UnsafePromise<C>()

    conn.push(listOf(suspend { connA.cancel() }, suspend { connB.cancel() }, suspend { connC.cancel() }))

    fun <A> onError(
      error: Throwable,
      connB: SuspendConnection,
      connC: SuspendConnection,
      promise: UnsafePromise<A>
    ): Unit {
      if (active.getAndSet(false)) { // if an error finishes first, stop the race.
        suspend { connB.cancel() }.startCoroutineUnintercepted(Continuation(ctx + SuspendConnection.uncancellable + NonCancellable) { r2 ->
          suspend { connC.cancel() }.startCoroutineUnintercepted(Continuation(ctx + SuspendConnection.uncancellable + NonCancellable) { r3 ->
            conn.pop()

            val errorResult = r2.fold({
              r3.fold({ error }, { e3 -> Platform.composeErrors(error, e3) })
            }, { e2 ->
              r3.fold({ Platform.composeErrors(error, e2) }, { e3 -> Platform.composeErrors(error, e2, e3) })
            })

            cont.resumeWith(Result.failure(errorResult))
          })
        })
      } else {
        promise.complete(Result.failure(error))
      }
    }

    fa.startCoroutineCancellable(CancellableContinuation(ctx + jobA, connA) { result ->
      result.fold({ a ->
        if (active.getAndSet(false)) {
          conn.pop()
          cont.resumeWith(Result.success(RaceTriple.First(a, Fiber(promiseB, connB), Fiber(promiseC, connC))))
        } else {
          promiseA.complete(Result.success(a))
        }
      }, { error -> onError(error, connB, connC, promiseA) })
    })

    fb.startCoroutineCancellable(CancellableContinuation(ctx + jobB, connB) { result ->
      result.fold({ b ->
        if (active.getAndSet(false)) {
          conn.pop()
          cont.resumeWith(Result.success(RaceTriple.Second(Fiber(promiseA, connA), b, Fiber(promiseC, connC))))
        } else {
          promiseB.complete(Result.success(b))
        }
      }, { error -> onError(error, connA, connC, promiseB) })
    })

    fc.startCoroutineCancellable(CancellableContinuation(ctx + jobC, connC) { result ->
      result.fold({ c ->
        if (active.getAndSet(false)) {
          conn.pop()
          cont.resumeWith(Result.success(RaceTriple.Third(Fiber(promiseA, connA), Fiber(promiseB, connB), c)))
        } else {
          promiseC.complete(Result.success(c))
        }
      }, { error -> onError(error, connA, connB, promiseC) })
    })

    COROUTINE_SUSPENDED
  }
