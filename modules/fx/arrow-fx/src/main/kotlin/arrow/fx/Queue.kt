package arrow.fx

import arrow.Kind
import arrow.core.Tuple2
import arrow.core.toT
import arrow.core.identity
import arrow.fx.internal.IQueue
import arrow.fx.typeclasses.Concurrent
import arrow.fx.typeclasses.Fiber
import arrow.typeclasses.Applicative
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.Monad

/**
 * Lightweight, asynchronous queue for values of A in a Concurrent context F
 * A Queue can be implemented using 4 different strategies
 *
 * Bounded: Offering to a queue at capacity will cause the fiber making the call
 * to be suspended until the queue has space to receive the offer value
 *
 * TODO: Unbound: Always places values from offer on the queue
 *
 * TODO: Sliding: Offering to a queue at capacity will cause the oldest value in
 * the queue to be dropped before the offer value is enqueued
 *
 * TODO: Dropping Queue: Will fail to enqueue a value when queue at capacity returning false
 *
 * ported from Scala ZIO Queue implementation
 */
class Queue<F, A> private constructor(val capacity: Int, private val ref: Ref<F, State<F, A>>, private val CF: Concurrent<F>) :
  Concurrent<F> by CF {

  fun size(): Kind<F, Int> = ref.get().flatMap { it.size() }

  /**
   * A Queue can be in three states
   * Deficit:
   *  Contains a queue of values and a queue of suspended fibers
   *  waiting to take once a value becomes available
   * Surplus:
   *  Contains a queue of values and a queue of suspended fibers
   *  waiting to offer once there is room (if the queue is bounded)
   * Shutdown:
   *  Holds no values or promises for suspended calls,
   *  an offer or take in Shutdown state creates a QueueShutdown error
   */
  internal sealed class State<F, out A> {
    abstract fun size(): Kind<F, Int>

    internal data class Deficit<F, A>(val takers: IQueue<Promise<F, A>>, val AP: Applicative<F>, val shutdownHook: Kind<F, Unit>) : State<F, A>() {
      override fun size(): Kind<F, Int> = AP.just(-takers.length())
    }

    internal data class Surplus<F, A>(val queue: IQueue<A>, val putters: IQueue<Tuple2<A, Promise<F, Unit>>>, val AP: Applicative<F>, val shutdownHook: Kind<F, Unit>) : State<F, A>() {
      override fun size(): Kind<F, Int> = AP.just(queue.length() + putters.length())
    }

    internal data class Shutdown<F>(val AE: ApplicativeError<F, Throwable>) : State<F, Nothing>() {
      override fun size(): Kind<F, Int> = AE.raiseError(QueueShutdown)
    }
  }

  fun offer(a: A): Kind<F, Unit> {
    val use: (Promise<F, Unit>, State<F, A>) -> Tuple2<Kind<F, Unit>, State<F, A>> = { p, state ->
      state.fold(
        ifSurplus = {
          it.run {
            if (queue.length() < capacity && putters.isEmpty())
              p.complete(Unit) toT copy(queue = queue.enqueue(a))
            else
              unit() toT it.copy(putters = putters.enqueue(a toT p))
          }
        },
        ifDeficit = {
          it.takers.dequeueOption().fold(
            { p.complete(Unit) toT State.Surplus(IQueue.empty<A>().enqueue(a), IQueue.empty(), CF, it.shutdownHook) },
            { (taker, takers) ->
              taker.complete(a).followedBy(p.complete(Unit)) toT it.copy(takers = takers)
            }
          )
        },
        ifShutdown = { p.error(QueueShutdown) toT state }
      )
    }

    val release: (Unit, Promise<F, Unit>) -> Kind<F, Unit> =
      { _, p -> removePutter(p) }

    return Promise.bracket(ref, use, release, CF)
  }

  fun take(): Kind<F, A> {
    val use: (Promise<F, A>, State<F, A>) -> Tuple2<Kind<F, Unit>, State<F, A>> = { p, state ->
      state.fold(
        ifSurplus = { surplus ->
          surplus.queue.dequeueOption().fold(
            ifEmpty = {
              surplus.putters.dequeueOption().fold(
                { just(Unit) toT State.Deficit(IQueue.empty<Promise<F, A>>().enqueue(p), CF, surplus.shutdownHook) },
                { (putter, putters) ->
                  val (a, prom) = putter
                  (prom.complete(Unit).followedBy(p.complete(a))) toT surplus.copy(
                    queue = IQueue.empty(),
                    putters = putters
                  )
                }
              )
            },
            ifSome = { (a, q) ->
              surplus.putters.dequeueOption().fold(
                { p.complete(a) toT surplus.copy(queue = q) },
                { (putter, putters) ->
                  val (putVal, putProm) = putter
                  (putProm.complete(Unit).followedBy(p.complete(a))) toT surplus.copy(
                    queue = q.enqueue(putVal),
                    putters = putters
                  )
                })
            }
          )
        },
        ifDeficit = { deficit -> just(Unit) toT deficit.copy(takers = deficit.takers.enqueue(p)) },
        ifShutdown = { p.error(QueueShutdown) toT state }
      )
    }

    val release: (Unit, Promise<F, A>) -> Kind<F, Unit> =
      { _, p -> removeTaker(p) }
    return Promise.bracket(ref, use, release, CF)
  }

  /**
   * Waits until the queue is shutdown.
   * The `IO` returned by this method will not resume until the queue has been shutdown.
   * If the queue is already shutdown, the `IO` will resume right away.
   */
  fun awaitShutdown(): Kind<F, Unit> =
    Promise<F, Unit>(CF).flatMap { promise ->
      val io = promise.complete(Unit)
      ref.modify { state ->
        state.fold(
          ifSurplus = { it.copy(shutdownHook = it.shutdownHook.followedBy(io.unit())) toT unit() },
          ifDeficit = { it.copy(shutdownHook = it.shutdownHook.followedBy(io.unit())) toT unit() },
          ifShutdown = { state toT io.unit() }
        )
      }.flatten().followedBy(promise.get())
    }

  /**
   * Cancels any fibers that are suspended on `offer` or `take`.
   * Future calls to `offer*` and `take*` will be interrupted immediately.
   */
  fun shutdown(): Kind<F, Unit> = ref.modify { state ->
    state.fold(
      ifSurplus = {
        if (it.putters.isEmpty()) State.Shutdown(CF) toT it.shutdownHook
        else {
          val forked = forkAll(it.putters.toList()
            .map { (_, p) -> p.error(QueueShutdown) }).flatMap { it.join() }
          State.Shutdown(CF) toT (forked.followedBy(it.shutdownHook))
        }
      },
      ifDeficit = {
        if (it.takers.isEmpty()) State.Shutdown(CF) toT it.shutdownHook
        else {
          val forked = forkAll(it.takers.toList()
            .map { it.error(QueueShutdown) }).flatMap { it.join() }
          State.Shutdown(CF) toT (forked.followedBy(it.shutdownHook))
        }
      },
      ifShutdown = { state toT unit() }
    )
  }.flatten()

  companion object {
    fun <F, A> bounded(capacity: Int, CF: Concurrent<F>): Kind<F, Queue<F, A>> = CF.run {
      Ref<State<F, A>>(State.Surplus(IQueue.empty(), IQueue.empty(), this, unit())).map {
        Queue(capacity, it, this)
      }
    }

    internal fun <F, A, C> State<F, A>.fold(
      ifSurplus: (State.Surplus<F, A>) -> C,
      ifDeficit: (State.Deficit<F, A>) -> C,
      ifShutdown: (State.Shutdown<F>) -> C
    ) =
      when (this) {
        is State.Surplus -> ifSurplus(this)
        is State.Deficit -> ifDeficit(this)
        is State.Shutdown -> ifShutdown(this)
      }
  }

  private fun <A> removeTaker(taker: Promise<F, A>): Kind<F, Unit> =
    ref.update { state ->
      state.fold(
        ifSurplus = ::identity,
        ifDeficit = { it.run { copy(takers.filterNot { t -> t == taker }) } },
        ifShutdown = ::identity
      )
    }

  private fun removePutter(putter: Promise<F, Unit>): Kind<F, Unit> =
    ref.update { state ->
      state.fold(
        ifSurplus = { it.run { copy(putters = putters.filterNot { p -> p == putter }) } },
        ifDeficit = ::identity,
        ifShutdown = ::identity
      )
    }
}

object QueueShutdown : RuntimeException() {
  override fun fillInStackTrace(): Throwable = this
}

// Write fiber Concurrent typeclass??
fun <F, A, B, C> Fiber<F, A>.zipwith(fb: Fiber<F, B>, M: Monad<F>, f: (Tuple2<A, B>) -> C): Fiber<F, C> {
  val fib: Kind<F, C> = M.map(this@zipwith.join(), fb.join(), f)
  return Fiber(fib, M.run { this@zipwith.cancel().followedBy(fb.cancel()) })
}

// Move somewhere else??
fun <F, A> Concurrent<F>.forkAll(iter: Iterable<Kind<F, A>>): Kind<F, Fiber<F, List<A>>> {
  val initial = just(emptyList<A>()).fork()
  return iter.fold(initial) { accFiberIO, elemIO ->
    tupled(accFiberIO, elemIO.fork()).map { (asFiber, elem) ->
      asFiber.zipwith(elem, this) { (xs, x) -> listOf(x) + xs }
    }
  }
}
