package arrow.effects.internal

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import arrow.core.Tuple2
import arrow.effects.ForIO
import arrow.effects.IOConnection
import arrow.effects.IO
import arrow.effects.IOOf
import arrow.effects.typeclasses.Fiber
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.CoroutineContext

interface IORacePair {

  /**
   * Race two tasks concurrently within a new [IO].
   * Race results in a winner and the other, yet to finish task running in a [Fiber].
   *
   * ```kotlin:ank:playground
   * import arrow.effects.ForIO
   * import arrow.effects.IO
   * import arrow.effects.typeclasses.Fiber
   * import kotlinx.coroutines.Dispatchers
   * import java.lang.RuntimeException
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val result = IO.racePair<Int, String>(Dispatchers.Default, IO.never, IO { "I won the race" }).flatMap {
   *     it.fold(
   *       { IO.raiseError<Int>(RuntimeException("IO.never cannot win")) },
   *       { (_: Fiber<ForIO, Int>, res: String) -> IO.just(res) }
   *     )
   *   }
   *   //sampleEnd
   *   println(IO.unsafeRunBlocking(result))
   * }
   * ```
   *
   * @param ctx [CoroutineContext] to execute the source [IO] on.
   * @param fa task to participate in the race
   * @param fb task to participate in the race
   * @return [IO] either [Left] with product of the winner's result [fa] and still running task [fb],
   *   or [Right] with product of running task [fa] and the winner's result [fb].
   */
  fun <A, B> racePair(ctx: CoroutineContext, fa: IOOf<A>, fb: IOOf<B>): IO<Either<Tuple2<A, Fiber<ForIO, B>>, Tuple2<Fiber<ForIO, A>, B>>> =
    IO.async { conn, cb ->
      val active = AtomicBoolean(true)

      val upstreamCancelToken = IO.defer { if (conn.isCanceled()) IO.unit else conn.cancel() }

      val connA = IOConnection()
      connA.push(upstreamCancelToken)
      val promiseA = UnsafePromise<A>()

      val connB = IOConnection()
      connB.push(upstreamCancelToken)
      val promiseB = UnsafePromise<B>()

      conn.pushPair(connA, connB)

      IORunLoop.startCancelable(IOForkedStart(fa, ctx), connA) { either: Either<Throwable, A> ->
        either.fold({ error ->
          if (active.getAndSet(false)) { // if an error finishes first, stop the race.
            IORunLoop.start(connB.cancel()) { r2 ->
              conn.pop()
              cb(Left(r2.fold({ Platform.composeErrors(error, it) }, { error })))
            }
          } else {
            promiseA.complete(Left(error))
          }
        }, { a ->
          if (active.getAndSet(false)) {
            conn.pop()
            cb(Right(Left(Tuple2(a, IOFiber(promiseB, connB)))))
          } else {
            promiseA.complete(Right(a))
          }
        })
      }

      IORunLoop.startCancelable(IOForkedStart(fb, ctx), connB) { either: Either<Throwable, B> ->
        either.fold({ error ->
          if (active.getAndSet(false)) { // if an error finishes first, stop the race.
            IORunLoop.start(connA.cancel()) { r2 ->
              conn.pop()
              cb(Left(r2.fold({ Platform.composeErrors(error, it) }, { error })))
            }
          } else {
            promiseB.complete(Left(error))
          }
        }, { b ->
          if (active.getAndSet(false)) {
            conn.pop()
            cb(Right(Right(Tuple2(IOFiber(promiseA, connA), b))))
          } else {
            promiseB.complete(Right(b))
          }
        })
      }
    }
}
