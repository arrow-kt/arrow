package arrow.fx.coroutines.stream

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.extensions.option.monad.flatten
import arrow.core.orElse
import arrow.fx.coroutines.ComputationPool
import arrow.fx.coroutines.ForkAndForget
import arrow.fx.coroutines.Platform
import arrow.fx.coroutines.Semaphore
import arrow.fx.coroutines.stream.concurrent.NoneTerminatedQueue
import arrow.fx.coroutines.stream.concurrent.Queue
import arrow.fx.coroutines.stream.concurrent.SignallingAtomic
import arrow.fx.coroutines.uncancellable
import kotlin.coroutines.CoroutineContext

// stops the join evaluation
// all the streams will be terminated. If err is supplied, that will get attached to any error currently present
suspend fun <O> stop(
  done: SignallingAtomic<Option<Option<Throwable>>>,
  outputQ: NoneTerminatedQueue<Chunk<O>>,
  rslt: Option<Throwable>
): Unit {
  done.update {
    when (it) {
      is Some -> {
        it.t.map { e -> Some(Platform.composeErrors(e, rslt.orNull())) }
          .orElse { Some(rslt) }
      }
      else -> Some(rslt)
    }
  }

  outputQ.enqueue1(None)
}

internal suspend fun <O> decrementRunning(
  done: SignallingAtomic<Option<Option<Throwable>>>,
  outputQ: NoneTerminatedQueue<Chunk<O>>,
  running: SignallingAtomic<Int>
): Unit =
  running.modify { n ->
    val now = n - 1
    Pair(now, (if (now == 0) suspend { stop(done, outputQ, None) } else suspend { Unit }))
  }.invoke()

internal suspend fun incrementRunning(running: SignallingAtomic<Int>): Unit =
  running.update { it + 1 }

// runs inner stream, each stream is forked. terminates when killSignal is true if fails will enq in queue failure
// note that supplied scope's resources must be leased before the inner stream forks the execution to another thread
// and that it must be released once the inner stream terminates or fails.
internal suspend fun <O> runInner(
  ctx: CoroutineContext,
  inner: Stream<O>,
  done: SignallingAtomic<Option<Option<Throwable>>>,
  outputQ: NoneTerminatedQueue<Chunk<O>>,
  running: SignallingAtomic<Int>,
  available: Semaphore,
  outerScope: Scope
): Unit =
  uncancellable {
    when (val lease = outerScope.lease()) {
      null -> throw Throwable("Outer scope is closed during inner stream startup")
      else -> {
        available.acquire()
        incrementRunning(running)
        ForkAndForget(ctx) {
          val e = Either.catch {
            inner.chunks()
              .effectMap { s ->
                outputQ.enqueue1(Some(s))
              }
              .interruptWhen(done.map { it.isDefined() }) // must be AFTER enqueue to the sync queue, otherwise the process may hang to enq last item while being interrupted
              .drain()
          }.swap().orNull()
          val e2 = lease.cancel().swap().orNull()
          available.release()
          Platform.composeErrors(e, e2)?.let { err ->
            stop(done, outputQ, Some(err))
          }
          decrementRunning(done, outputQ, running)
        }

        Unit
      }
    }
  }

// runs the outer stream, interrupts when kill == true, and then decrements the `running`
internal suspend fun <O> Stream<Stream<O>>.runOuter(
  ctx: CoroutineContext,
  done: SignallingAtomic<Option<Option<Throwable>>>,
  outputQ: NoneTerminatedQueue<Chunk<O>>,
  running: SignallingAtomic<Int>,
  available: Semaphore
): Unit {
  val r = Either.catch {
    this@runOuter.flatMap { inner ->
      Stream.getScope.effectMap { outerScope ->
        runInner(ctx, inner, done, outputQ, running, available, outerScope)
      }
    }.interruptWhen(done.map { it.isDefined() })
      .drain()
  }

  when (r) {
    is Either.Right -> decrementRunning(done, outputQ, running)
    is Either.Left -> stop(done, outputQ, Some(r.a))
      .also { decrementRunning(done, outputQ, running) }
  }
}

// awaits when all streams (outer + inner) finished,
// and then collects result of the stream (outer + inner) execution
internal suspend fun signalResult(done: SignallingAtomic<Option<Option<Throwable>>>): Unit =
  done.get().flatten().fold({ Unit }, { throw it })

/**
 * Non-deterministically merges a stream of streams (`outer`) in to a single stream,
 * opening at most `maxOpen` streams at any point in time.
 *
 * The outer stream is evaluated and each resulting inner stream is run concurrently,
 * up to `maxOpen` stream. Once this limit is reached, evaluation of the outer stream
 * is paused until one or more inner streams finish evaluating.
 *
 * When the outer stream stops gracefully, all inner streams continue to run,
 * resulting in a stream that will stop when all inner streams finish
 * their evaluation.
 *
 * When the outer stream fails, evaluation of all inner streams is interrupted
 * and the resulting stream will fail with same failure.
 *
 * When any of the inner streams fail, then the outer stream and all other inner
 * streams are interrupted, resulting in stream that fails with the error of the
 * stream that caused initial failure.
 *
 * Finalizers on each inner stream are run at the end of the inner stream,
 * concurrently with other stream computations.
 *
 * Finalizers on the outer stream are run after all inner streams have been pulled
 * from the outer stream but not before all inner streams terminate -- hence finalizers on the outer stream will run
 * AFTER the LAST finalizer on the very last inner stream.
 *
 * Finalizers on the returned stream are run after the outer stream has finished
 * and all open inner streams have finished.
 *
 * ```kotlin:ank:playground
 * import arrow.fx.coroutines.IOPool
 * import arrow.fx.coroutines.milliseconds
 * import arrow.fx.coroutines.sleep
 * import arrow.fx.coroutines.stream.*
 *
 * //sampleStart
 * suspend fun main(): Unit =
 *   Stream.range(100 downTo 1)
 *     .map { i ->
 *       Stream.effect_ {
 *         sleep((i * 10).milliseconds)
 *         println("Task $i running on ${Thread.currentThread().name}")
 *       }
 *     }
 *     .parJoin(10, IOPool)
 *     .drain()
 * //sampleEnd
 * ```
 *
 * @param maxOpen Maximum number of open inner streams at any time. Must be > 0.
 */
fun <O> Stream<Stream<O>>.parJoin(
  maxOpen: Int,
  ctx: CoroutineContext = ComputationPool
): Stream<O> {
  require(maxOpen > 0) { "maxOpen must be > 0, was: $maxOpen" }

  return Stream.effect {
    val done = SignallingAtomic<Option<Option<Throwable>>>(None)
    val available = Semaphore(maxOpen.toLong())
    val running = SignallingAtomic(1) // starts with 1 because outer stream is running by default

    // sync queue assures we won't overload heap when resulting stream is not able to catchup with inner streams
    // stops the join evaluation
    // all the streams will be terminated. If err is supplied, that will get attached to any error currently present
    val outputQ = Queue.synchronousNoneTerminated<Chunk<O>>()

    Stream.bracket({
      ForkAndForget(ctx) { runOuter(ctx, done, outputQ, running, available) }
    }, {
      stop(done, outputQ, None)

      running.discrete() // Await everyone stop running
        .dropWhile { it > 0 }
        .take(1)
        .drain()

      signalResult(done)
    }).flatMap {
      outputQ.dequeue()
        .flatMap(Stream.Companion::chunk)
    }
  }.flatten()
}

/** Like [parJoin] but races all inner streams simultaneously without limit. */
fun <O> Stream<Stream<O>>.parJoinUnbounded(ctx: CoroutineContext = ComputationPool): Stream<O> =
  parJoin(Int.MAX_VALUE, ctx)
