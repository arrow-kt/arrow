package arrow.fx

import arrow.Kind
import arrow.core.Tuple2
import arrow.core.identity
import arrow.core.toT
import arrow.fx.internal.IQueue
import arrow.fx.typeclasses.Concurrent
import arrow.typeclasses.Applicative

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

class ConcurrentQueue<F, A> internal constructor(
  private val strategy: SurplusStrategy<F, A>,
  private val ref: Ref<F, Queue.State<F, A>>,
  private val CF: Concurrent<F>
) : Concurrent<F> by CF,
  Queue<F, A> {

  override fun size(): Kind<F, Int> = ref.get().flatMap { it.size() }

  override fun offer(a: A): Kind<F, Unit> {
    val use: (Promise<F, Unit>, Queue.State<F, A>) -> Tuple2<Kind<F, Unit>, Queue.State<F, A>> = { p, state ->
      state.fold(
        ifSurplus = { surplus -> strategy.handleSurplus(p, surplus, a) },
        ifDeficit = { deficit ->
          deficit.takers.dequeueOption().fold(
            { strategy.handleSurplus(p, Queue.State.Surplus(IQueue.empty(), IQueue.empty(), CF, deficit.shutdownHook), a) },
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

  override fun take(): Kind<F, A> {
    val use: (Promise<F, A>, Queue.State<F, A>) -> Tuple2<Kind<F, Unit>, Queue.State<F, A>> = { p, state ->
      state.fold(
        ifSurplus = { surplus ->
          surplus.queue.dequeueOption().fold(
            ifEmpty = {
              surplus.putters.dequeueOption().fold(
                { just(Unit) toT Queue.State.Deficit(IQueue.empty<Promise<F, A>>().enqueue(p), CF, surplus.shutdownHook) },
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
  override fun awaitShutdown(): Kind<F, Unit> =
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
  override fun shutdown(): Kind<F, Unit> = ref.modify { state ->
    state.fold(
      ifSurplus = { surplus ->
        if (surplus.putters.isEmpty()) Queue.State.Shutdown(CF) toT surplus.shutdownHook
        else {
          val forked = surplus.putters.toList()
            .parTraverse { (_, p) -> p.error(QueueShutdown) }
          Queue.State.Shutdown(CF) toT (forked.followedBy(surplus.shutdownHook))
        }
      },
      ifDeficit = { deficit ->
        if (deficit.takers.isEmpty()) Queue.State.Shutdown(CF) toT deficit.shutdownHook
        else {
          val forked = deficit.takers.toList().parTraverse { p -> p.error(QueueShutdown) }
          Queue.State.Shutdown(CF) toT (forked.followedBy(deficit.shutdownHook))
        }
      },
      ifShutdown = { state toT unit() }
    )
  }.flatten()

  private fun <F, A, C> Queue.State<F, A>.fold(
    ifSurplus: (Queue.State.Surplus<F, A>) -> C,
    ifDeficit: (Queue.State.Deficit<F, A>) -> C,
    ifShutdown: (Queue.State.Shutdown<F>) -> C
  ) =
    when (this) {
      is Queue.State.Surplus -> ifSurplus(this)
      is Queue.State.Deficit -> ifDeficit(this)
      is Queue.State.Shutdown -> ifShutdown(this)
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

  internal sealed class SurplusStrategy<F, A> {
    abstract fun handleSurplus(p: Promise<F, Unit>, surplus: Queue.State.Surplus<F, A>, a: A): Tuple2<Kind<F, Unit>, Queue.State<F, A>>

    data class Bounded<F, A>(val capacity: Int, val AP: Applicative<F>) : SurplusStrategy<F, A>() {
      override fun handleSurplus(p: Promise<F, Unit>, surplus: Queue.State.Surplus<F, A>, a: A): Tuple2<Kind<F, Unit>, Queue.State<F, A>> =
        surplus.run {
          if (queue.length() < capacity && putters.isEmpty())
            p.complete(Unit) toT copy(queue = queue.enqueue(a))
          else
            AP.unit() toT copy(putters = putters.enqueue(a toT p))
        }
    }

    data class Sliding<F, A>(val capacity: Int, val AP: Applicative<F>) : SurplusStrategy<F, A>() {
      override fun handleSurplus(p: Promise<F, Unit>, surplus: Queue.State.Surplus<F, A>, a: A): Tuple2<Kind<F, Unit>, Queue.State<F, A>> =
        surplus.run {
          val nextQueue =
            if (queue.length() < capacity) queue.enqueue(a)
            else queue.dequeue().b.enqueue(a)
          p.complete(Unit) toT copy(queue = nextQueue)
        }
    }

    data class Dropping<F, A>(val capacity: Int, val AP: Applicative<F>) : SurplusStrategy<F, A>() {
      override fun handleSurplus(p: Promise<F, Unit>, surplus: Queue.State.Surplus<F, A>, a: A): Tuple2<Kind<F, Unit>, Queue.State<F, A>> =
        surplus.run {
          val nextQueue = if (queue.length() < capacity) queue.enqueue(a) else queue
          p.complete(Unit) toT copy(queue = nextQueue)
        }
    }

    data class Unbounded<F, A>(val AP: Applicative<F>) : SurplusStrategy<F, A>() {
      override fun handleSurplus(p: Promise<F, Unit>, surplus: Queue.State.Surplus<F, A>, a: A): Tuple2<Kind<F, Unit>, Queue.State<F, A>> =
        surplus.run {
          p.complete(Unit) toT copy(queue = queue.enqueue(a))
        }
    }
  }

  companion object {
    fun <F, A> empty(CF: Concurrent<F>): Kind<F, Queue<F, A>> = CF.run {
      Ref<Queue.State<F, A>>(Queue.State.Surplus(IQueue.empty(), IQueue.empty(), this, unit())).map {
        ConcurrentQueue(ConcurrentQueue.SurplusStrategy.Unbounded(this), it, this)
      }
    }
  }
}
