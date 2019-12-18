package arrow.fx

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import arrow.core.internal.AtomicBooleanW
import arrow.fx.internal.IOFiber
import arrow.fx.internal.IOForkedStart
import arrow.fx.internal.Platform
import arrow.fx.internal.UnsafePromise
import arrow.fx.typeclasses.Fiber
import kotlin.coroutines.CoroutineContext

/** Mix-in to enable `parMapN` 2-arity on IO's companion directly. */
interface IORaceTriple {

  /**
   * Race three tasks concurrently within a new [IO].
   * Race results in a winner and the others, yet to finish task running in a [Fiber].
   *
   * ```kotlin:ank:playground
   * import arrow.fx.*
   * import arrow.fx.extensions.fx
   * import kotlinx.coroutines.Dispatchers
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val result = IO.fx {
   *     val raceResult = !IO.raceTriple(Dispatchers.Default, never<Int>(), just("Hello World!"), never<Double>())
   *     raceResult.fold(
   *       { _, _, _ -> "never cannot win before complete" },
   *       { _, winner, _ -> winner },
   *       { _, _, _ -> "never cannot win before complete" }
   *     )
   *   }
   *   //sampleEnd
   *
   *   val r = result.unsafeRunSync()
   *   println("Race winner result is: $r")
   * }
   * ```
   *
   * @param ctx [CoroutineContext] to execute the source [IO] on.
   * @param ioA task to participate in the race
   * @param ioB task to participate in the race
   * @param ioC task to participate in the race
   * @return [RaceTriple]
   *
   * @see [arrow.fx.typeclasses.Concurrent.raceN] for a simpler version that cancels losers.
   */
  fun <A, B, C> raceTriple(ctx: CoroutineContext, ioA: IOOf<A>, ioB: IOOf<B>, ioC: IOOf<C>): IO<RaceTriple<ForIO, A, B, C>> =
    BIO.Async { conn, cb ->
      val active = AtomicBooleanW(true)

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
}
