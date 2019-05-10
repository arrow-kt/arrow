package arrow.effects.suspended.fx

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import arrow.core.Tuple2
import arrow.effects.internal.Platform
import arrow.effects.internal.UnsafePromise
import arrow.effects.internal.asyncContinuation
import arrow.effects.typeclasses.Fiber
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.startCoroutine
import kotlin.coroutines.suspendCoroutine

fun <A, B> Fx.Companion.racePair(ctx: CoroutineContext, fa: FxOf<A>, fb: FxOf<B>): Fx<Either<Tuple2<A, Fiber<ForFx, B>>, Tuple2<Fiber<ForFx, A>, B>>> =
  Fx.async { conn, cb ->
    val active = AtomicBoolean(true)

    val upstreamCancelToken = Fx.defer { if (conn.isCanceled()) Fx.unit else conn.cancel() }

    val connA = FxConnection()
    connA.push(upstreamCancelToken)
    val promiseA = UnsafePromise<A>()

    val connB = FxConnection()
    connB.push(upstreamCancelToken)
    val promiseB = UnsafePromise<B>()

    conn.pushPair(connA, connB)

    suspend {
      suspendCoroutine { ca: Continuation<A> ->
        FxRunLoop.startCancelable(fa, connA, ctx) { either: Either<Throwable, A> ->
          either.fold({ error ->
            ca.resumeWith(Result.failure(error))
          }, { a ->
            ca.resumeWith(Result.success(a))
          })
        }
      }
    }.startCoroutine(asyncContinuation(ctx) { either ->
      either.fold({ error ->
        if (active.getAndSet(false)) { // if an error finishes first, stop the race.
          FxRunLoop.start(connB.cancel()) { r2 ->
            conn.pop()
            cb(Left(r2.fold({ Platform.composeErrors(error, it) }, { error })))
          }
        } else {
          promiseA.complete(Left(error))
        }
      }, { a ->
        if (active.getAndSet(false)) {
          conn.pop()
          cb(Right(Left(Tuple2(a, FxFiber(promiseB, connB)))))
        } else {
          promiseA.complete(Right(a))
        }
      })
    })

    suspend {
      suspendCoroutine { ca: Continuation<B> ->
        FxRunLoop.startCancelable(fb, connB, ctx) { either: Either<Throwable, B> ->
          either.fold({ error ->
            ca.resumeWith(Result.failure(error))
          }, { b ->
            ca.resumeWith(Result.success(b))
          })
        }
      }
    }.startCoroutine(asyncContinuation(ctx) { either ->
      either.fold({ error ->
        if (active.getAndSet(false)) { // if an error finishes first, stop the race.
          FxRunLoop.start(connA.cancel()) { r2 ->
            conn.pop()
            cb(Left(r2.fold({ Platform.composeErrors(error, it) }, { error })))
          }
        } else {
          promiseB.complete(Left(error))
        }
      }, { b ->
        if (active.getAndSet(false)) {
          conn.pop()
          cb(Right(Right(Tuple2(FxFiber(promiseA, connA), b))))
        } else {
          promiseB.complete(Right(b))
        }
      })
    })
  }
