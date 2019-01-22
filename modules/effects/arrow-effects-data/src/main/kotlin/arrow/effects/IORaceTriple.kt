package arrow.effects

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import arrow.core.Tuple3
import arrow.effects.internal.IOFiber
import arrow.effects.internal.Platform
import arrow.effects.internal.UnsafePromise
import arrow.effects.internal.asyncContinuation
import arrow.effects.typeclasses.Fiber
import arrow.effects.typeclasses.RaceTriple
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.startCoroutine
import kotlin.coroutines.suspendCoroutine

/**
 * Race three tasks concurrently within a new [IO].
 * Race results in a winner and the others, yet to finish task running in a [Fiber].
 *
 * ```kotlin:ank:playground
 * import arrow.effects.*
 * import arrow.effects.extensions.io.async.async
 * import arrow.effects.extensions.io.monad.binding
 * import arrow.effects.typeclasses.*
 * import kotlinx.coroutines.Dispatchers
 * import java.lang.RuntimeException
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   binding {
 *     val promise = Promise.uncancelable<ForIO, Int>(IO.async()).bind()
 *     val raceTriple = IO.raceTriple(Dispatchers.Default, promise.get(), IO.unit, IO.never).bind()
 *     raceTriple.fold(
 *       { IO.raiseError<Int>(RuntimeException("Promise.get cannot win before complete")) },
 *       { (a: Fiber<ForIO, Int>, _, _) -> promise.complete(1).flatMap { a.join() } },
 *       { IO.raiseError<Int>(RuntimeException("never cannot win before complete")) }
 *     ).bind()
 *   }.unsafeRunSync() == 1
 *   //sampleEnd
 * }
 * ```
 *
 * @param ctx [CoroutineContext] to execute the source [IO] on.
 * @param ioA task to participate in the race
 * @param ioB task to participate in the race
 * @param ioC task to participate in the race
 * @return [RaceTriple]
 *
 * @see [arrow.effects.typeclasses.Concurrent.raceN] for a simpler version that cancels losers.
 */
fun <A, B, C> IO.Companion.raceTriple(ctx: CoroutineContext, ioA: IOOf<A>, ioB: IOOf<B>, ioC: IOOf<C>): IO<RaceTriple<ForIO, A, B, C>> =
  IO.async { conn, cb ->
    val active = AtomicBoolean(true)

    val upstreamCancelToken = IO.defer { if (conn.isCanceled()) IO.unit else conn.cancel() }

    val connA = IOConnection()
    connA.push(upstreamCancelToken)
    val promiseA = UnsafePromise<A>()

    val connB = IOConnection()
    connB.push(upstreamCancelToken)
    val promiseB = UnsafePromise<B>()

    val connC = IOConnection()
    connC.push(upstreamCancelToken)
    val promiseC = UnsafePromise<C>()

    conn.push(connA.cancel(), connB.cancel(), connC.cancel())

    val a: suspend () -> A = {
      suspendCoroutine { ca: Continuation<A> ->
        IORunLoop.startCancelable(ioA, connA) { either: Either<Throwable, A> ->
          either.fold({ error ->
            ca.resumeWith(Result.failure(error))
          }, { a ->
            ca.resumeWith(Result.success(a))
          })
        }
      }
    }

    val b: suspend () -> B = {
      suspendCoroutine { ca: Continuation<B> ->
        IORunLoop.startCancelable(ioB, connB) { either: Either<Throwable, B> ->
          either.fold({ error ->
            ca.resumeWith(Result.failure(error))
          }, { b ->
            ca.resumeWith(Result.success(b))
          })
        }
      }
    }

    val c: suspend () -> C = {
      suspendCoroutine { ca: Continuation<C> ->
        IORunLoop.startCancelable(ioC, connC) { either: Either<Throwable, C> ->
          either.fold({ error ->
            ca.resumeWith(Result.failure(error))
          }, { c ->
            ca.resumeWith(Result.success(c))
          })
        }
      }
    }

    a.startCoroutine(asyncContinuation(ctx) { either ->
      either.fold({ error ->
        if (active.getAndSet(false)) { //if an error finishes first, stop the race.
          connB.cancel().fix().unsafeRunAsync { r2 ->
            connC.cancel().fix().unsafeRunAsync { r3 ->
              conn.pop()
              val errorResult = r2.fold({ e2 ->
                r3.fold({ e3 -> Platform.composeErrors(error, e2, e3) }, { Platform.composeErrors(error, e2) })
              }, {
                r3.fold({ e3 -> Platform.composeErrors(error, e3) }, { error })
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
          cb(Right(Left(Tuple3(a, IOFiber(promiseB, connB), IOFiber(promiseC, connC)))))
        } else {
          promiseA.complete(Right(a))
        }
      })
    })


    b.startCoroutine(asyncContinuation(ctx) { either ->
      either.fold({ error ->
        if (active.getAndSet(false)) { //if an error finishes first, stop the race.
          connA.cancel().fix().unsafeRunAsync { r2 ->
            connC.cancel().fix().unsafeRunAsync { r3 ->
              conn.pop()
              val errorResult = r2.fold({ e2 ->
                r3.fold({ e3 -> Platform.composeErrors(error, e2, e3) }, { Platform.composeErrors(error, e2) })
              }, {
                r3.fold({ e3 -> Platform.composeErrors(error, e3) }, { error })
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
          cb(Right(Right(Left(Tuple3(IOFiber(promiseA, connA), b, IOFiber(promiseC, connC))))))
        } else {
          promiseB.complete(Right(b))
        }
      })
    })

    c.startCoroutine(asyncContinuation(ctx) { either ->
      either.fold({ error ->
        if (active.getAndSet(false)) { //if an error finishes first, stop the race.
          connA.cancel().fix().unsafeRunAsync { r2 ->
            connB.cancel().fix().unsafeRunAsync { r3 ->
              conn.pop()
              val errorResult = r2.fold({ e2 ->
                r3.fold({ e3 -> Platform.composeErrors(error, e2, e3) }, { Platform.composeErrors(error, e2) })
              }, {
                r3.fold({ e3 -> Platform.composeErrors(error, e3) }, { error })
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
          cb(Right(Right(Right(Tuple3(IOFiber(promiseA, connA), IOFiber(promiseB, connB), c)))))
        } else {
          promiseC.complete(Right(c))
        }
      })
    })

  }