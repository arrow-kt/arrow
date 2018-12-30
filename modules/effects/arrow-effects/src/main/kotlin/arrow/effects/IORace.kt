package arrow.effects

import arrow.core.*
import arrow.effects.internal.asyncContinuation
import arrow.effects.internal.unsafe
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
 * {: data-executable='true'}
 *
 * ```kotlin:ank
 * import arrow.effects.*
 * import arrow.effects.instances.io.async.async
 * import arrow.effects.instances.io.concurrent.racePair
 * import arrow.effects.instances.io.monad.binding
 * import arrow.effects.typeclasses.Fiber
 * import kotlinx.coroutines.Dispatchers
 * import java.lang.RuntimeException
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   binding {
 *     val promise = Promise.uncancelable<ForIO, Int>(IO.async()).bind()
 *     val eitherGetOrUnit = racePair(Dispatchers.Default, promise.get, IO.unit).bind()
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
 * @see raceN for a simpler version that cancels loser.
 */
fun <A, B> IO.Companion.racePair(ctx: CoroutineContext, ioA: IOOf<A>, ioB: IOOf<B>): IO<Either<Tuple2<A, Fiber<ForIO, B>>, Tuple2<Fiber<ForIO, A>, B>>> =
  IO.async { conn, cb ->
    val active = AtomicBoolean(true)

    // Cancelable connection for the left value
    val connA = IOConnection()
    val promiseA = Promise.unsafe<Either<Throwable, A>>()

    // Cancelable connection for the right value
    val connB = IOConnection()
    val promiseB = Promise.unsafe<Either<Throwable, B>>()

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
            cb(Left(r2.fold({ CompositeFailure(error, it) }, { error })))
          }
        } else {
          promiseA.complete(error.left())
        }
      }, { a ->
        if (active.getAndSet(false)) {
          conn.pop()
          cb(Tuple2(a, Fiber(promiseB, connB)).left().right())
        } else {
          promiseA.complete(a.right())
        }
      })
    })

    b.startCoroutine(asyncContinuation(ctx) { either ->
      either.fold({ error ->
        if (active.getAndSet(false)) { //if an error finishes first, stop the race.
          connA.cancel().fix().unsafeRunAsync { r2 ->
            conn.pop()
            cb(Left(r2.fold({ CompositeFailure(error, it) }, { error })))
          }
        } else {
          promiseB.complete(error.left())
        }
      }, { b ->
        if (active.getAndSet(false)) {
          conn.pop()
          cb(Tuple2(Fiber(promiseA, connA), b).right().right())
        } else {
          promiseB.complete(b.right())
        }
      })
    })

  }

internal class CompositeFailure(first: Throwable, second: Throwable) :
  Throwable("Two exceptions were thrown, first $first: ${first.message}. second: $second: ${second.message}", first)

internal fun <A> Fiber(promise: Promise<ForId, Either<Throwable, A>>, conn: IOConnection): Fiber<ForIO, A> {
  val join: IO<A> = IO.defer {
    try {
      promise.get.value().fold(
        { IO.raiseError<A>(it) },
        { IO.just(it) }
      )
    } catch (t: Throwable) {
      IO.raiseError<A>(t)
    }
  }

  val cancel = conn.cancel()

  return Fiber(join, cancel)
}
