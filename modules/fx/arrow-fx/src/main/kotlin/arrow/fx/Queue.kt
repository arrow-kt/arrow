package arrow.fx

import arrow.Kind
import arrow.core.Tuple2
import arrow.core.toT
import arrow.core.identity
import arrow.fx.internal.IQueue
import arrow.fx.typeclasses.Concurrent
import arrow.typeclasses.Applicative
import arrow.typeclasses.ApplicativeError

/**
 * Lightweight, asynchronous queue for values of A in a Concurrent context F
 * A Queue can be implemented using 4 different strategies
 *
 * Bounded: Offering to a bounded queue at capacity will cause the fiber making
 * the call to be suspended until the queue has space to receive the offer value
 *
 * Dropping: Offering to a dropping queue at capacity will cause the offered
 * value to be discarded
 *
 * Sliding: Offering to a sliding queue at capacity will cause the value at the
 * front of the queue to be discarded to make room for the offered value
 *
 * Unbounded: An unbounded queue has no notion of capacity and is bound only by
 * exhausting the memory limits of the runtime
 *
 * ported from [Scala ZIO Queue](https://zio.dev/docs/datatypes/datatypes_queue)
 * implementation
 */
class Queue<F, A> private constructor(private val strategy: SurplusStrategy<F, A>, private val ref: Ref<F, State<F, A>>, private val CF: Concurrent<F>) :
  Concurrent<F> by CF {

  fun size(): Kind<F, Int> = ref.get().flatMap { it.size() }

  fun offer(a: A): Kind<F, Unit> {
    val use: (Promise<F, Unit>, State<F, A>) -> Tuple2<Kind<F, Unit>, State<F, A>> = { p, state ->
      state.fold(
        ifSurplus = { surplus -> strategy.handleSurplus(p, surplus, a) },
        ifDeficit = { deficit ->
          deficit.takers.dequeueOption().fold(
            { strategy.handleSurplus(p, State.Surplus(IQueue.empty(), IQueue.empty(), CF, deficit.shutdownHook), a) },
            { (taker, takers) ->
              taker.complete(a).followedBy(p.complete(Unit)) toT deficit.copy(takers = takers)
            }
          )
        },
        ifShutdown = { shutdown -> p.error(QueueShutdown) toT shutdown }
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
        ifShutdown = { shutdown -> p.error(QueueShutdown) toT shutdown }
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
      val complete = promise.complete(Unit)
      ref.modify { state ->
        state.fold(
          ifSurplus = { it.copy(shutdownHook = it.shutdownHook.followedBy(complete)) toT unit() },
          ifDeficit = { it.copy(shutdownHook = it.shutdownHook.followedBy(complete)) toT unit() },
          ifShutdown = { state toT complete }
        )
      }.flatten().followedBy(promise.get())
    }

  /**
   * Cancels any fibers that are suspended on `offer` or `take`.
   * Future calls to `offer*` and `take*` will be interrupted immediately.
   */
  fun shutdown(): Kind<F, Unit> = ref.modify { state ->
    state.fold(
      ifSurplus = { surplus ->
        if (surplus.putters.isEmpty()) State.Shutdown(CF) toT surplus.shutdownHook
        else {
          val forked = surplus.putters.toList()
            .parTraverse { (_, p) -> p.error(QueueShutdown) }
          State.Shutdown(CF) toT (forked.followedBy(surplus.shutdownHook))
        }
      },
      ifDeficit = { deficit ->
        if (deficit.takers.isEmpty()) State.Shutdown(CF) toT deficit.shutdownHook
        else {
          val forked = deficit.takers.toList().parTraverse { p -> p.error(QueueShutdown) }
          State.Shutdown(CF) toT (forked.followedBy(deficit.shutdownHook))
        }
      },
      ifShutdown = { state toT unit() }
    )
  }.flatten()

  companion object {
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
    fun <F, A> bounded(capacity: Int, CF: Concurrent<F>): Kind<F, Queue<F, A>> = CF.run {
      Ref<State<F, A>>(State.Surplus(IQueue.empty(), IQueue.empty(), this, unit())).map {
        Queue(SurplusStrategy.Bounded(capacity, this), it, this)
      }
    }

    fun <F, A> sliding(capacity: Int, CF: Concurrent<F>): Kind<F, Queue<F, A>> = CF.fx.concurrent {
      !just(capacity).ensure(
        { IllegalArgumentException("Sliding queue must have a capacity greater than 0") },
        { it > 0 }
      )
      val ref = !Ref<State<F, A>>(State.Surplus(IQueue.empty(), IQueue.empty(), CF, unit()))
      Queue(SurplusStrategy.Sliding(capacity, CF), ref, CF)
    }

    fun <F, A> dropping(capacity: Int, CF: Concurrent<F>): Kind<F, Queue<F, A>> = CF.run {
      Ref<State<F, A>>(State.Surplus(IQueue.empty(), IQueue.empty(), this, unit())).map {
        Queue(SurplusStrategy.Dropping(capacity, this), it, this)
      }
    }

    fun <F, A> unbounded(CF: Concurrent<F>): Kind<F, Queue<F, A>> = CF.run {
      Ref<State<F, A>>(State.Surplus(IQueue.empty(), IQueue.empty(), this, unit())).map {
        Queue(SurplusStrategy.Unbounded(this), it, this)
      }
    }
  }

  private fun <F, A, C> State<F, A>.fold(
    ifSurplus: (State.Surplus<F, A>) -> C,
    ifDeficit: (State.Deficit<F, A>) -> C,
    ifShutdown: (State.Shutdown<F>) -> C
  ) =
    when (this) {
      is State.Surplus -> ifSurplus(this)
      is State.Deficit -> ifDeficit(this)
      is State.Shutdown -> ifShutdown(this)
    }

  private fun <A> removeTaker(taker: Promise<F, A>): Kind<F, Unit> =
    ref.update { state ->
      state.fold(
        ifSurplus = ::identity,
        ifDeficit = { deficit -> deficit.run { copy(takers.filterNot { t -> t == taker }) } },
        ifShutdown = ::identity
      )
    }

  private fun removePutter(putter: Promise<F, Unit>): Kind<F, Unit> =
    ref.update { state ->
      state.fold(
        ifSurplus = { surplus -> surplus.run { copy(putters = putters.filterNot { p -> p == putter }) } },
        ifDeficit = ::identity,
        ifShutdown = ::identity
      )
    }

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
  private sealed class State<F, out A> {
    abstract fun size(): Kind<F, Int>

    data class Deficit<F, A>(val takers: IQueue<Promise<F, A>>, val AP: Applicative<F>, val shutdownHook: Kind<F, Unit>) : State<F, A>() {
      override fun size(): Kind<F, Int> = AP.just(-takers.length())
    }

    data class Surplus<F, A>(val queue: IQueue<A>, val putters: IQueue<Tuple2<A, Promise<F, Unit>>>, val AP: Applicative<F>, val shutdownHook: Kind<F, Unit>) : State<F, A>() {
      override fun size(): Kind<F, Int> = AP.just(queue.length() + putters.length())
    }

    data class Shutdown<F>(val AE: ApplicativeError<F, Throwable>) : State<F, Nothing>() {
      override fun size(): Kind<F, Int> = AE.raiseError(QueueShutdown)
    }
  }

  private sealed class SurplusStrategy<F, A> {
    abstract fun handleSurplus(p: Promise<F, Unit>, surplus: State.Surplus<F, A>, a: A): Tuple2<Kind<F, Unit>, State<F, A>>

    data class Bounded<F, A>(val capacity: Int, val AP: Applicative<F>) : SurplusStrategy<F, A>() {
      override fun handleSurplus(p: Promise<F, Unit>, surplus: State.Surplus<F, A>, a: A): Tuple2<Kind<F, Unit>, State<F, A>> =
        surplus.run {
          if (queue.length() < capacity && putters.isEmpty())
            p.complete(Unit) toT copy(queue = queue.enqueue(a))
          else
            AP.unit() toT copy(putters = putters.enqueue(a toT p))
        }
    }

    data class Sliding<F, A>(val capacity: Int, val AP: Applicative<F>) : SurplusStrategy<F, A>() {
      override fun handleSurplus(p: Promise<F, Unit>, surplus: State.Surplus<F, A>, a: A): Tuple2<Kind<F, Unit>, State<F, A>> =
        surplus.run {
          val nextQueue =
            if (queue.length() < capacity) queue.enqueue(a)
            else queue.dequeue().b.enqueue(a)
          p.complete(Unit) toT copy(queue = nextQueue)
        }
    }

    data class Dropping<F, A>(val capacity: Int, val AP: Applicative<F>) : SurplusStrategy<F, A>() {
      override fun handleSurplus(p: Promise<F, Unit>, surplus: State.Surplus<F, A>, a: A): Tuple2<Kind<F, Unit>, State<F, A>> =
        surplus.run {
          val nextQueue = if (queue.length() < capacity) queue.enqueue(a) else queue
          p.complete(Unit) toT copy(queue = nextQueue)
        }
    }

    data class Unbounded<F, A>(val AP: Applicative<F>) : SurplusStrategy<F, A>() {
      override fun handleSurplus(p: Promise<F, Unit>, surplus: State.Surplus<F, A>, a: A): Tuple2<Kind<F, Unit>, State<F, A>> =
        surplus.run {
          p.complete(Unit) toT copy(queue = queue.enqueue(a))
        }
    }
  }
}

object QueueShutdown : RuntimeException() {
  override fun fillInStackTrace(): Throwable = this
}
