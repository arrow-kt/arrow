package arrow.fx.coroutines

import arrow.core.Either
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.intrinsics.intercepted
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED

typealias RacePair<A, B> = Either<Pair<A, Fiber<B>>, Pair<Fiber<A>, B>>

fun <A, B, C> Either<Pair<A, Fiber<B>>, Pair<Fiber<A>, B>>.fold(ifLeft: (A, Fiber<B>) -> C, ifRight: (Fiber<A>, B) -> C): C =
  fold({ (a, b) -> ifLeft(a, b) }, { (a, b) -> ifRight(a, b) })

suspend fun <A, B> racePair(fa: suspend () -> A, fb: suspend () -> B): RacePair<A, B> =
  racePair(Dispatchers.Default, fa, fb)

/**
 * Races two tasks concurrently within a new suspend fun.
 * Race results in a winner and the other, yet to finish task running in a [Fiber].
 *
 * ```kotlin:ank:playground
 * import arrow.fx.coroutines.*
 * import arrow.core.Either
 *
 * suspend fun main(): Unit {
 *   //sampleStart
 *   val res = racePair({ never<Int>() }, { "Hello World!" })
 *   val r = when(res) {
 *     is Either.Left -> "never cannot win race"
 *     is Either.Right -> res.b
 *   }
 *   //sampleEnd
 *   println("Race winner result is: $r")
 * }
 * ```
 *
 * @param ctx [CoroutineContext] to execute the source [fa] & [fb] on.
 * @param fa task to participate in the race
 * @param fb task to participate in the race
 * @return either [Either.Left] with product of the winner's result [ƒa] and still running task [fb],
 *   or [Either.Right] with product of running task [ƒa] and the winner's result [fb].
 *
 * @see [arrow.fx.coroutines.raceN] for a simpler version that cancels loser.
 */
@Deprecated("Will be removed since it leaks Fiber, and breaks structured concurrency. Replace with select")
suspend fun <A, B> racePair(
  ctx: CoroutineContext,
  fa: suspend () -> A,
  fb: suspend () -> B
): RacePair<A, B> =
  suspendCancellableCoroutine { cont ->
    if (cont.isActive) {
      val disposable = suspend {
        oldRacePair(ctx, fa, fb)
      }.startCoroutineCancellable(CancellableContinuation(cont.context, cont::resumeWith))
      cont.invokeOnCancellation { disposable() }
    }
  }

suspend fun <A, B> oldRacePair(
  ctx: CoroutineContext,
  fa: suspend () -> A,
  fb: suspend () -> B
): RacePair<A, B> =
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

    conn.pushPair(connA, connB)

    fa.startCoroutineCancellable(CancellableContinuation(ctx + jobA, connA) { result ->
      result.fold({ a ->
        if (active.getAndSet(false)) {
          conn.pop()
          cont.resumeWith(Result.success(Either.Left(Pair(a, Fiber(promiseB, connB)))))
        } else {
          promiseA.complete(Result.success(a))
        }
      }, { error ->
        if (active.getAndSet(false)) { // if an error finishes first, stop the race.
          suspend { connB.cancel() }.startCoroutineUnintercepted(Continuation(ctx + SuspendConnection.uncancellable + NonCancellable) { r2 ->
            conn.pop()
            cont.resumeWith(Result.failure(r2.fold({ error }, { Platform.composeErrors(error, it) })))
          })
        } else {
          promiseA.complete(Result.failure(error))
        }
      })
    })

    fb.startCoroutineCancellable(CancellableContinuation(ctx + jobB, connB) { result ->
      result.fold({ b ->
        if (active.getAndSet(false)) {
          conn.pop()
          cont.resumeWith(Result.success(Either.Right(Pair(Fiber(promiseA, connA), b))))
        } else {
          promiseB.complete(Result.success(b))
        }
      }, { error ->
        if (active.getAndSet(false)) { // if an error finishes first, stop the race.
          suspend { connA.cancel() }.startCoroutineUnintercepted(Continuation(ctx + SuspendConnection.uncancellable + NonCancellable) { r2 ->
            conn.pop()
            cont.resumeWith(Result.failure(r2.fold({ error }, { Platform.composeErrors(error, it) })))
          })
        } else {
          promiseB.complete(Result.failure(error))
        }
      })
    })

    COROUTINE_SUSPENDED
  }
