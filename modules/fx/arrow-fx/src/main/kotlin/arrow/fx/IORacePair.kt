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
interface IORacePair {

  /**
   * Race two tasks concurrently within a new [IO].
   * Race results in a winner and the other, yet to finish task running in a [Fiber].
   *
   * ```kotlin:ank:playground
   * import arrow.fx.*
   * import arrow.fx.extensions.fx
   * import kotlinx.coroutines.Dispatchers
   *
   * fun main(args: Array<String>) {
   *     //sampleStart
   *     val result = IO.fx {
   *       val racePair = !IO.racePair(Dispatchers.Default, never<Int>(), just("Hello World!"))
   *       racePair.fold(
   *         { _, _ -> "never cannot win race" },
   *         { _, winner -> winner }
   *       )
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
   * @return [IO] either [Left] with product of the winner's result [ioA] and still running task [ioB],
   *   or [Right] with product of running task [ioA] and the winner's result [ioB].
   *
   * @see [arrow.fx.typeclasses.Concurrent.raceN] for a simpler version that cancels loser.
   */
  fun <A, B> racePair(ctx: CoroutineContext, ioA: IOOf<A>, ioB: IOOf<B>): IO<RacePair<ForIO, A, B>> =
    BIO.Async { conn, cb ->
      val active = AtomicBooleanW(true)

      val upstreamCancelToken = IO.defer { if (conn.isCanceled()) IO.unit else conn.cancel() }

      // Cancelable connection for the left value
      val connA = IOConnection()
      connA.push(upstreamCancelToken)
      val promiseA = UnsafePromise<A>()

      // Cancelable connection for the right value
      val connB = IOConnection()
      connB.push(upstreamCancelToken)
      val promiseB = UnsafePromise<B>()

      conn.pushPair(connA, connB)

      IORunLoop.startCancelable(IOForkedStart(ioA, ctx), connA) { either: Either<Throwable, A> ->
        either.fold({ error ->
          if (active.getAndSet(false)) { // if an error finishes first, stop the race.
            connB.cancel().fix().unsafeRunAsync { r2 ->
              conn.pop()
              cb(Left(r2.fold({ Platform.composeErrors(error, it) }, { error })))
            }
          } else {
            promiseA.complete(Left(error))
          }
        }, { a ->
          if (active.getAndSet(false)) {
            conn.pop()
            cb(Right(RacePair.First(a, IOFiber(promiseB, connB))))
          } else {
            promiseA.complete(Right(a))
          }
        })
      }

      IORunLoop.startCancelable(IOForkedStart(ioB, ctx), connB) { either: Either<Throwable, B> ->
        either.fold({ error ->
          if (active.getAndSet(false)) { // if an error finishes first, stop the race.
            connA.cancel().fix().unsafeRunAsync { r2 ->
              conn.pop()
              cb(Left(r2.fold({ Platform.composeErrors(error, it) }, { error })))
            }
          } else {
            promiseB.complete(Left(error))
          }
        }, { b ->
          if (active.getAndSet(false)) {
            conn.pop()
            cb(Right(RacePair.Second(IOFiber(promiseA, connA), b)))
          } else {
            promiseB.complete(Right(b))
          }
        })
      }
    }
}
