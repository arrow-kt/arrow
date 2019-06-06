package arrow.effects

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import arrow.effects.internal.IOFiber
import arrow.effects.internal.IOForkedStart
import arrow.effects.internal.Platform
import arrow.effects.internal.UnsafePromise
import arrow.effects.typeclasses.Fiber
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.CoroutineContext

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
 *   fx.monad {
 *     val promise = Promise.uncancelable<ForIO, Int>(IO.async()).bind()
 *     val raceTriple = IO.raceTriple(Dispatchers.Default, promise.get(), IO.unit, IO.never).bind()
 *     raceTriple.fold(
 *       { _, _, _ -> IO.raiseError<Int>(RuntimeException("Promise.get cannot win before complete")) },
 *       { a: Fiber<ForIO, Int>, _, _ -> promise.complete(1).flatMap { a.join() } },
 *       { _, _, _ -> IO.raiseError<Int>(RuntimeException("never cannot win before complete")) }
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
  async { conn, cb ->
    val active = AtomicBoolean(true)

    val upstreamCancelToken = defer { if (conn.isCanceled()) unit else conn.cancel() }

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

    IORunLoop.startCancelable(IOForkedStart(ioA, ctx), connA) { either: Either<Throwable, A> ->
      either.fold({ error ->
        if (active.getAndSet(false)) { // if an error finishes first, stop the race.
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
          cb(Right(RaceTriple.First(a, IOFiber(promiseB, connB), IOFiber(promiseC, connC))))
        } else {
          promiseA.complete(Right(a))
        }
      })
    }

    IORunLoop.startCancelable(IOForkedStart(ioB, ctx), connB) { either: Either<Throwable, B> ->
      either.fold({ error ->
        if (active.getAndSet(false)) { // if an error finishes first, stop the race.
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
          cb(Right(RaceTriple.Second(IOFiber(promiseA, connA), b, IOFiber(promiseC, connC))))
        } else {
          promiseB.complete(Right(b))
        }
      })
    }

    IORunLoop.startCancelable(IOForkedStart(ioC, ctx), connC) { either: Either<Throwable, C> ->
      either.fold({ error ->
        if (active.getAndSet(false)) { // if an error finishes first, stop the race.
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
          cb(Right(RaceTriple.Third(IOFiber(promiseA, connA), IOFiber(promiseB, connB), c)))
        } else {
          promiseC.complete(Right(c))
        }
      })
    }
  }
