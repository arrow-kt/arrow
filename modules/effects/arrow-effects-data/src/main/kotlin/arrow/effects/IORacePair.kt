package arrow.effects

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import arrow.core.Tuple2
import arrow.effects.internal.IOFiber
import arrow.effects.internal.Platform
import arrow.effects.internal.UnsafePromise
import arrow.effects.internal.asyncContinuation
import arrow.effects.typeclasses.Fiber
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.startCoroutine
import kotlin.coroutines.suspendCoroutine

/**
 * Race two tasks concurrently within a new [IO].
 * Race results in a winner and the other, yet to finish task running in a [Fiber].
 *
 * ```kotlin:ank:playground
 * import arrow.effects.*
 * import arrow.effects.extensions.io.async.async
 * import arrow.effects.extensions.io.concurrent.racePair
 * import arrow.effects.extensions.io.monad.binding
 * import arrow.effects.typeclasses.Fiber
 * import kotlinx.coroutines.Dispatchers
 * import java.lang.RuntimeException
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   binding {
 *     val promise = Promise.uncancelable<ForIO, Int>(IO.async()).bind()
 *     val eitherGetOrUnit = Dispatchers.Default.racePair(promise.get(), IO.unit).bind()
 *     eitherGetOrUnit.fold(
 *       { IO.raiseError<Int>(RuntimeException("Promise.get cannot win before complete")) },
 *       { (a: Fiber<ForIO, Int>, _) -> promise.complete(1).flatMap { a.join() } }
 *     ).bind()
 *   }.unsafeRunSync() == 1
 *   //sampleEnd
 * }
 * ```
 *
 * @param ctx [CoroutineContext] to execute the source [IO] on.
 * @param ioA task to participate in the race
 * @param ioB task to participate in the race
 * @return [IO] either [Left] with product of the winner's result [ioA] and still running task [ioB],
 *   or [Right] with product of running task [ioA] and the winner's result [ioB].
 *
 * @see [arrow.effects.typeclasses.Concurrent.raceN] for a simpler version that cancels loser.
 */
fun <A, B> IO.Companion.racePair(ctx: CoroutineContext, ioA: IOOf<A>, ioB: IOOf<B>): IO<Either<Tuple2<A, Fiber<ForIO, B>>, Tuple2<Fiber<ForIO, A>, B>>> =
  IO.async { conn, cb ->
    val active = AtomicBoolean(true)

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

    a.startCoroutine(asyncContinuation(ctx) { either ->
      either.fold({ error ->
        if (active.getAndSet(false)) { //if an error finishes first, stop the race.
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
          cb(Right(Left(Tuple2(a, IOFiber(promiseB, connB)))))
        } else {
          promiseA.complete(Right(a))
        }
      })
    })


    b.startCoroutine(asyncContinuation(ctx) { either ->
      either.fold({ error ->
        if (active.getAndSet(false)) { //if an error finishes first, stop the race.
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
          cb(Right(Right(Tuple2(IOFiber(promiseA, connA), b))))
        } else {
          promiseB.complete(Right(b))
        }
      })
    })

  }
