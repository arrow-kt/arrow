package arrow.fx.coroutines

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.intrinsics.intercepted
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED

sealed class Race3<out A, out B, out C> {
  data class First<A>(val winner: A) : Race3<A, Nothing, Nothing>()
  data class Second<B>(val winner: B) : Race3<Nothing, B, Nothing>()
  data class Third<C>(val winner: C) : Race3<Nothing, Nothing, C>()

  inline fun <D> fold(
    ifA: (A) -> D,
    ifB: (B) -> D,
    ifC: (C) -> D
  ): D = when (this) {
    is First -> ifA(winner)
    is Second -> ifB(winner)
    is Third -> ifC(winner)
  }
}

/**
 * Races the participants [fa], [fb] & [fc] in parallel on the [ComputationPool].
 * The winner of the race cancels the other participants.
 * Cancelling the operation cancels all participants.
 *
 * @see raceN for the same function that can race on any [CoroutineContext].
 */
suspend fun <A, B, C> raceN(
  fa: suspend () -> A,
  fb: suspend () -> B,
  fc: suspend () -> C
): Race3<A, B, C> = raceN(ComputationPool, fa, fb, fc)

/**
 * Races the participants [fa], [fb] & [fc] on the provided [CoroutineContext].
 * The winner of the race cancels the other participants.
 * Cancelling the operation cancels all participants.
 *
 * **WARNING**: operations run in parallel depending on the capabilities of the provided [CoroutineContext].
 * We ensure they start in sequence so it's guaranteed to finish on a single threaded context.
 *
 * @see raceN for a function that ensures operations run in parallel on the [ComputationPool].
 */
suspend fun <A, B, C> raceN(
  ctx: CoroutineContext,
  fa: suspend () -> A,
  fb: suspend () -> B,
  fc: suspend () -> C
): Race3<A, B, C> {
  fun onSuccess(
    isActive: AtomicBooleanW,
    main: SuspendConnection,
    other2: SuspendConnection,
    other3: SuspendConnection,
    cb: (Result<Race3<A, B, C>>) -> Unit,
    r: Race3<A, B, C>
  ): Unit = if (isActive.getAndSet(false)) {
    // Continue on the winners Context/Thread
    suspend { other2.cancel() }.startCoroutineUnintercepted(Continuation(ctx + SuspendConnection.uncancellable) { r2 ->
      suspend { other3.cancel() }.startCoroutineUnintercepted(Continuation(ctx + SuspendConnection.uncancellable) { r3 ->
        main.pop()
        r2.fold({
          r3.fold({ cb(Result.success(r)) }, { e -> cb(Result.failure(e)) })
        }, { e ->
          r3.fold({ cb(Result.failure(e)) }, { e2 -> cb(Result.failure(Platform.composeErrors(e, e2))) })
        })
      })
    })
  } else Unit

  fun onError(
    active: AtomicBooleanW,
    cb: (Result<Nothing>) -> Unit,
    main: SuspendConnection,
    other2: SuspendConnection,
    other3: SuspendConnection,
    err: Throwable
  ): Unit = if (active.getAndSet(false)) {
    // Continue on the winners Context/Thread
    suspend { other2.cancel() }.startCoroutineUnintercepted(Continuation(ctx + SuspendConnection.uncancellable) { r2 ->
      suspend { other3.cancel() }.startCoroutineUnintercepted(Continuation(ctx + SuspendConnection.uncancellable) { r3 ->
        main.pop()
        cb(
          Result.failure(
            r2.fold({
              r3.fold({
                err
              }, { err3 ->
                Platform.composeErrors(err, err3)
              })
            }, { err2 ->
              r3.fold({
                Platform.composeErrors(err, err2)
              }, { err3 ->
                Platform.composeErrors(err, err2, err3)
              })
            })
          )
        )
      })
    })
  } else Unit

  return suspendCoroutineUninterceptedOrReturn { cont ->
    val conn = cont.context[SuspendConnection] ?: SuspendConnection.uncancellable
    val cont = cont.intercepted()

    val active = AtomicBooleanW(true)
    val connA = SuspendConnection()
    val connB = SuspendConnection()
    val connC = SuspendConnection()

    conn.push(listOf(suspend { connA.cancel() }, suspend { connB.cancel() }, suspend { connC.cancel() }))

    fa.startCoroutineCancellable(CancellableContinuation(ctx, connA) { result ->
      result.fold({
        onSuccess(active, conn, connB, connC, cont::resumeWith, Race3.First(it))
      }, {
        onError(active, cont::resumeWith, conn, connB, connC, it)
      })
    })

    fb.startCoroutineCancellable(CancellableContinuation(ctx, connB) { result ->
      result.fold({
        onSuccess(active, conn, connA, connC, cont::resumeWith, Race3.Second(it))
      }, {
        onError(active, cont::resumeWith, conn, connA, connC, it)
      })
    })

    fc.startCoroutineCancellable(CancellableContinuation(ctx, connC) { result ->
      result.fold({
        onSuccess(active, conn, connA, connB, cont::resumeWith, Race3.Third(it))
      }, {
        onError(active, cont::resumeWith, conn, connA, connB, it)
      })
    })

    COROUTINE_SUSPENDED
  }
}
