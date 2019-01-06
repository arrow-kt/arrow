package arrow.effects

import arrow.core.*
import arrow.effects.internal.ArrowInternalException
import arrow.effects.typeclasses.Fiber
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
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
 *     val eitherGetOrUnit = racePair(Dispatchers.Default, promise.get(), IO.unit).bind()
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
    val promiseA = IOUnsafePromise<A>()

    // Cancelable connection for the right value
    val connB = IOConnection()
    connB.push(upstreamCancelToken)
    val promiseB = IOUnsafePromise<B>()

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
          promiseA.complete(Left(error))
        }
      }, { a ->
        if (active.getAndSet(false)) {
          conn.pop()
          cb(Right(Left(Tuple2(a, Fiber(promiseB, connB)))))
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
            cb(Left(r2.fold({ CompositeFailure(error, it) }, { error })))
          }
        } else {
          promiseB.complete(Left(error))
        }
      }, { b ->
        if (active.getAndSet(false)) {
          conn.pop()
          cb(Right(Right(Tuple2(Fiber(promiseA, connA), b))))
        } else {
          promiseB.complete(Right(b))
        }
      })
    })

  }

/**
 * Meant for internal use for [IO.Companion.racePair] and [startF].
 */
internal class IOUnsafePromise<A> {

  private sealed class State<out A> {
    object Empty : State<Nothing>()
    data class Waiting<A>(val joiners: List<(Either<Throwable, A>) -> Unit>) : State<A>()
    data class Full<A>(val a: Either<Throwable, A>) : State<A>()
  }

  private val state: AtomicReference<State<A>> = AtomicReference(State.Empty)

  fun get(cb: (Either<Throwable, A>) -> Unit): Unit {
    tailrec fun go(): Unit = when (val oldState = state.get()) {
      State.Empty -> if (state.compareAndSet(oldState, State.Waiting(listOf(cb)))) Unit else go()
      is State.Waiting -> if (state.compareAndSet(oldState, State.Waiting(oldState.joiners + cb))) Unit else go()
      is State.Full -> cb(oldState.a)
    }

    go()
  }

  fun complete(value: Either<Throwable, A>): Unit {
    tailrec fun go(): Unit = when (val oldState = state.get()) {
      State.Empty -> if (state.compareAndSet(oldState, State.Full(value))) Unit else go()
      is State.Waiting -> {
        if (state.compareAndSet(oldState, State.Full(value))) oldState.joiners.forEach { it(value) }
        else go()
      }
      is State.Full -> throw ArrowInternalException()
    }

    go()
  }

  fun remove(cb: (Either<Throwable, A>) -> Unit) = when (val oldState = state.get()) {
    State.Empty -> Unit
    is State.Waiting -> state.set(State.Waiting(oldState.joiners - cb))
    is State.Full -> Unit
  }

}

internal fun <A> Fiber(promise: IOUnsafePromise<A>, conn: IOConnection): Fiber<ForIO, A> {
  val join: IO<A> = IO.async { conn2, cb ->
    conn2.push(IO { promise.remove(cb) })
    conn.push(conn2.cancel())

    promise.get { a ->
      cb(a)
      conn2.pop()
      conn.pop()
    }
  }

  return Fiber(join, conn.cancel())
}

/**
 * [arrow.core.Continuation] to run coroutine on `ctx` and link result to callback [cc].
 * Use [asyncContinuation] to run suspended functions within a context `ctx` and pass the result to [cc].
 */
internal fun <A> asyncContinuation(ctx: CoroutineContext, cc: (Either<Throwable, A>) -> Unit): arrow.core.Continuation<A> =
  object : arrow.core.Continuation<A> {
    override val context: CoroutineContext = ctx

    override fun resume(value: A) {
      cc(value.right())
    }

    override fun resumeWithException(exception: Throwable) {
      cc(exception.left())
    }

  }

internal class CompositeFailure(first: Throwable, second: Throwable) :
  Throwable("Two exceptions were thrown, first $first: ${first.message}. second: $second: ${second.message}", first)