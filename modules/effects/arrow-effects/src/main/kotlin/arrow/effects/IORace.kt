package arrow.effects

import arrow.core.*
import arrow.effects.internal.IOConnection
import arrow.effects.internal.asyncContinuation
import arrow.effects.internal.unsafe
import arrow.effects.typeclasses.Fiber
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.startCoroutine
import kotlin.coroutines.suspendCoroutine

typealias Pair<A, B> = Either<Tuple2<A, Fiber<ForIO, B>>, Tuple2<Fiber<ForIO, A>, B>>

fun <A, B> IO.Companion.racePair(ctx: CoroutineContext, ioA: IOOf<A>, ioB: IOOf<B>): IO<Pair<A, B>> =
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
        IORunLoop.startCancelable(ioA.fix(), connA) { either: Either<Throwable, A> ->
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
        IORunLoop.startCancelable(ioB.fix(), connB) { either: Either<Throwable, B> ->
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
