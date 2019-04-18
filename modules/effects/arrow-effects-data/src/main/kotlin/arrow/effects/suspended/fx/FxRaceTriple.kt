package arrow.effects.suspended.fx

import arrow.core.Left
import arrow.core.Right
import arrow.core.Tuple3
import arrow.effects.internal.Platform
import arrow.effects.internal.UnsafePromise
import arrow.effects.typeclasses.RaceTriple
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.CoroutineContext

fun <A, B, C> Fx.Companion.raceTriple(ctx: CoroutineContext, fa: FxOf<A>, fb: FxOf<B>, fc: FxOf<C>): Fx<RaceTriple<ForFx, A, B, C>> =
  Fx.async { conn, cb ->
    val active = AtomicBoolean(true)

    val upstreamCancelToken = Fx.defer { if (conn.isCanceled()) Fx.unit else conn.cancel() }

    val connA = FxConnection()
    connA.push(upstreamCancelToken)
    val promiseA = UnsafePromise<A>()

    val connB = FxConnection()
    connB.push(upstreamCancelToken)
    val promiseB = UnsafePromise<B>()

    val connC = FxConnection()
    connC.push(upstreamCancelToken)
    val promiseC = UnsafePromise<C>()

    conn.push(connA.cancel(), connB.cancel(), connC.cancel())

    FxRunLoop.startCancelable(fa, connA, ctx) { either ->
      either.fold({ error ->
        if (active.getAndSet(false)) { //if an error finishes first, stop the race.
          FxRunLoop.start(connB.cancel()) { r2 ->
            FxRunLoop.start(connC.cancel()) { r3 ->
              conn.pop()
              val errorResult = r2.fold(ifLeft = { e2 ->
                r3.fold(ifLeft = { e3 -> Platform.composeErrors(error, e2, e3) }, ifRight = { Platform.composeErrors(error, e2) })
              }, ifRight = {
                r3.fold(ifLeft = { e3 -> Platform.composeErrors(error, e3) }, ifRight = { error })
              })
              cb(Left(errorResult))
            }
          }
        } else {
          promiseA.complete(Left(error))
        }
      }, { a ->
        if (active.getAndSet(false)) {
          conn.pop()
          cb(Right(Left(Tuple3(a, FxFiber(promiseB, connB), FxFiber(promiseC, connC)))))
        } else {
          promiseA.complete(Right(a))
        }
      })
    }

    FxRunLoop.startCancelable(fb, connB) { either ->
      either.fold({ error ->
        if (active.getAndSet(false)) { //if an error finishes first, stop the race.
          FxRunLoop.start(connA.cancel()) { r2 ->
            FxRunLoop.start(connC.cancel()) { r3 ->
              conn.pop()
              val errorResult = r2.fold(ifLeft = { e2 ->
                r3.fold(ifLeft = { e3 -> Platform.composeErrors(error, e2, e3) }, ifRight = { Platform.composeErrors(error, e2) })
              }, ifRight = {
                r3.fold(ifLeft = { e3 -> Platform.composeErrors(error, e3) }, ifRight = { error })
              })
              cb(Left(errorResult))
            }
          }
        } else {
          promiseB.complete(Left(error))
        }
      }, { b ->
        if (active.getAndSet(false)) {
          conn.pop()
          cb(Right(Right(Left(Tuple3(FxFiber(promiseA, connA), b, FxFiber(promiseC, connC))))))
        } else {
          promiseB.complete(Right(b))
        }
      })
    }

    FxRunLoop.startCancelable(fc, connC) { either ->
      either.fold({ error ->
        if (active.getAndSet(false)) { //if an error finishes first, stop the race.
          FxRunLoop.start(connA.cancel()) { r2 ->
            FxRunLoop.start(connB.cancel()) { r3 ->
              conn.pop()
              val errorResult = r2.fold(ifLeft = { e2 ->
                r3.fold(ifLeft = { e3 -> Platform.composeErrors(error, e2, e3) }, ifRight = { Platform.composeErrors(error, e2) })
              }, ifRight = {
                r3.fold(ifLeft = { e3 -> Platform.composeErrors(error, e3) }, ifRight = { error })
              })
              cb(Left(errorResult))
            }
          }
        } else {
          promiseC.complete(Left(error))
        }
      }, { c ->
        if (active.getAndSet(false)) {
          conn.pop()
          cb(Right(Right(Right(Tuple3(FxFiber(promiseA, connA), FxFiber(promiseB, connB), c)))))
        } else {
          promiseC.complete(Right(c))
        }
      })
    }

  }
