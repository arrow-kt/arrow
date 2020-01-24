package arrow.fx

import arrow.core.internal.AtomicBooleanW
import arrow.fx.internal.IOFiber
import arrow.fx.internal.IOForkedStart
import arrow.fx.internal.Platform
import arrow.fx.internal.UnsafePromise
import arrow.fx.typeclasses.Fiber
import kotlin.coroutines.CoroutineContext

/** Mix-in to enable `raceTriple` on IO's companion directly. */
interface IORaceTriple {

  fun <E, A, B, C> raceTriple(ioA: IOOf<E, A>, ioB: IOOf<E, B>, ioC: IOOf<E, C>): IO<E, RaceTriple<IOPartialOf<E>, A, B, C>> =
    IO.raceTriple(IODispatchers.CommonPool, ioA, ioB, ioC)

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
  fun <E, A, B, C> raceTriple(ctx: CoroutineContext, ioA: IOOf<E, A>, ioB: IOOf<E, B>, ioC: IOOf<E, C>): IO<E, RaceTriple<IOPartialOf<E>, A, B, C>> =
    IO.Async(true) { conn, cb ->
      val active = AtomicBooleanW(true)

      val upstreamCancelToken = IO.defer { if (conn.isCanceled()) IO.unit else conn.cancel() }

      val connA = IOConnection()
      connA.push(upstreamCancelToken)
      val promiseA = UnsafePromise<E, A>()

      val connB = IOConnection()
      connB.push(upstreamCancelToken)
      val promiseB = UnsafePromise<E, B>()

      val connC = IOConnection()
      connC.push(upstreamCancelToken)
      val promiseC = UnsafePromise<E, C>()

      conn.push(connA.cancel(), connB.cancel(), connC.cancel())

      IORunLoop.startCancelable(IOForkedStart(ioA, ctx), connA) { either: IOResult<E, A> ->
        either.fold({ error ->
          if (active.getAndSet(false)) { // if an error finishes first, stop the race.
            connB.cancel().unsafeRunAsync { r2 ->
              connC.cancel().unsafeRunAsync { r3 ->
                conn.pop()
                val errorResult = r2.fold({ e2 ->
                  r3.fold({ e3 -> Platform.composeErrors(error, e2, e3) }, { Platform.composeErrors(error, e2) })
                }, {
                  r3.fold({ e3 -> Platform.composeErrors(error, e3) }, { error })
                })
                cb(IOResult.Exception(errorResult))
              }
            }
          } else {
            promiseA.complete(IOResult.Exception(error))
          }
        }, { e ->
          if (active.getAndSet(false)) { // if an error finishes first, stop the race.
            connB.cancel().unsafeRunAsync { r2 ->
              connC.cancel().unsafeRunAsync { r3 ->
                conn.pop()
                cb(IOResult.Error(e))
              }
            }
          } else {
            promiseA.complete(IOResult.Error(e))
          }
        }, { a ->
          if (active.getAndSet(false)) {
            conn.pop()
            cb(IOResult.Success(RaceTriple.First(a, IOFiber(promiseB, connB), IOFiber(promiseC, connC))))
          } else {
            promiseA.complete(IOResult.Success(a))
          }
        })
      }

      IORunLoop.startCancelable(IOForkedStart(ioB, ctx), connB) { either: IOResult<E, B> ->
        either.fold({ error ->
          if (active.getAndSet(false)) { // if an error finishes first, stop the race.
            connA.cancel().unsafeRunAsync { r2 ->
              connC.cancel().unsafeRunAsync { r3 ->
                conn.pop()
                val errorResult = r2.fold({ e2 ->
                  r3.fold({ e3 -> Platform.composeErrors(error, e2, e3) }, { Platform.composeErrors(error, e2) })
                }, {
                  r3.fold({ e3 -> Platform.composeErrors(error, e3) }, { error })
                })
                cb(IOResult.Exception(errorResult))
              }
            }
          } else {
            promiseB.complete(IOResult.Exception(error))
          }
        }, { e ->
          if (active.getAndSet(false)) { // if an error finishes first, stop the race.
            connA.cancel().unsafeRunAsync { r2 ->
              connC.cancel().unsafeRunAsync { r3 ->
                conn.pop()
                cb(IOResult.Error(e))
              }
            }
          } else {
            promiseB.complete(IOResult.Error(e))
          }
        }, { b ->
          if (active.getAndSet(false)) {
            conn.pop()
            cb(IOResult.Success(RaceTriple.Second(IOFiber(promiseA, connA), b, IOFiber(promiseC, connC))))
          } else {
            promiseB.complete(IOResult.Success(b))
          }
        })
      }

      IORunLoop.startCancelable(IOForkedStart(ioC, ctx), connC) { either: IOResult<E, C> ->
        either.fold({ error ->
          if (active.getAndSet(false)) { // if an error finishes first, stop the race.
            connA.cancel().unsafeRunAsync { r2 ->
              connB.cancel().unsafeRunAsync { r3 ->
                conn.pop()
                val errorResult = r2.fold({ e2 ->
                  r3.fold({ e3 -> Platform.composeErrors(error, e2, e3) }, { Platform.composeErrors(error, e2) })
                }, {
                  r3.fold({ e3 -> Platform.composeErrors(error, e3) }, { error })
                })
                cb(IOResult.Exception(errorResult))
              }
            }
          } else {
            promiseC.complete(IOResult.Exception(error))
          }
        }, { e ->
          if (active.getAndSet(false)) { // if an error finishes first, stop the race.
            connA.cancel().unsafeRunAsync { r2 ->
              connB.cancel().unsafeRunAsync { r3 ->
                conn.pop()
                //
                cb(IOResult.Error(e))
              }
            }
          } else {
            promiseC.complete(IOResult.Error(e))
          }
        }, { c ->
          if (active.getAndSet(false)) {
            conn.pop()
            cb(IOResult.Success(RaceTriple.Third(IOFiber(promiseA, connA), IOFiber(promiseB, connB), c)))
          } else {
            promiseC.complete(IOResult.Success(c))
          }
        })
      }
    }
}
