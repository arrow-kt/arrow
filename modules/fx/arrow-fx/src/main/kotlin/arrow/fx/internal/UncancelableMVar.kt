package arrow.fx.internal

import arrow.Kind
import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Right
import arrow.core.Some
import arrow.core.Tuple2
import arrow.fx.MVar
import arrow.fx.internal.UncancelableMVar.Companion.State.WaitForPut
import arrow.fx.internal.UncancelableMVar.Companion.State.WaitForTake
import arrow.fx.typeclasses.Async
import arrow.fx.typeclasses.rightUnit
import arrow.fx.typeclasses.unitCallback
import kotlinx.atomicfu.atomic

// [MVar] implementation for [Async] data types.
@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
internal class UncancelableMVar<F, A> private constructor(initial: State<A>, private val AS: Async<F>) : MVar<F, A>, Async<F> by AS {

  private val stateRef = atomic(initial)

  override fun put(a: A): Kind<F, Unit> =
    tryPut(a).flatMap { result ->
      if (result) unit()
      else asyncF { cb -> unsafePut(a, cb) }
    }

  override fun tryPut(a: A): Kind<F, Boolean> =
    defer { unsafeTryPut(a) }

  override fun take(): Kind<F, A> =
    tryTake().flatMap {
      it.fold({ asyncF(::unsafeTake) }, ::just)
    }

  override fun tryTake(): Kind<F, Option<A>> =
    defer(::unsafeTryTake)

  override fun read(): Kind<F, A> =
    async(::unsafeRead)

  override fun isEmpty(): Kind<F, Boolean> = later {
    when (stateRef.value) {
      is WaitForPut -> true
      is WaitForTake -> false
    }
  }

  override fun isNotEmpty(): Kind<F, Boolean> = later {
    when (stateRef.value) {
      is WaitForPut -> false
      is WaitForTake -> true
    }
  }

  private tailrec fun unsafeTryPut(a: A): Kind<F, Boolean> =
    when (val current = stateRef.value) {
      is WaitForTake -> justFalse
      is WaitForPut -> {
        val reads = current.reads
        val takes = current.takes
        var first: Listener<A>? = null
        val update: State<A> =
          if (takes.isEmpty()) {
            WaitForTake(a, IQueue.empty())
          } else {
            val (x, rest) = takes.dequeue()
            first = x
            if (rest.isEmpty()) State.empty()
            else WaitForPut(IQueue.empty(), rest)
          }

        if (!stateRef.compareAndSet(current, update)) unsafeTryPut(a) // retry
        else if ((first != null) || reads.nonEmpty()) streamPutAndReads(a, reads, first)
        else justTrue
      }
    }

  private tailrec fun unsafePut(a: A, onPut: Listener<Unit>): Kind<F, Unit> =
    when (val current = stateRef.value) {
      is WaitForTake -> {
        val update = WaitForTake(current.value, current.puts.enqueue(Tuple2(a, onPut)))
        if (!stateRef.compareAndSet(current, update)) unsafePut(a, onPut) // retry
        else unit()
      }
      is WaitForPut -> {
        val reads = current.reads
        val takes = current.takes
        var first: Listener<A>? = null
        val update: State<A> =
          if (takes.isEmpty()) {
            WaitForTake(a, IQueue.empty())
          } else {
            val (x, rest) = takes.dequeue()
            first = x
            if (rest.isEmpty()) State.empty()
            else WaitForPut(IQueue.empty(), rest)
          }

        if (!stateRef.compareAndSet(current, update)) unsafePut(a, onPut) // retry
        else streamPutAndReads(a, reads, first).map { onPut(rightUnit) }
      }
    }

  private tailrec fun unsafeTryTake(): Kind<F, Option<A>> =
    when (val current = stateRef.value) {
      is WaitForTake -> {
        val value = current.value
        val puts = current.puts
        if (puts.isEmpty()) {
          if (stateRef.compareAndSet(current, State.empty())) just(Some(value))
          else unsafeTryTake() // retry
        } else {
          val (x, xs) = puts.dequeue()
          val (ax, notify) = x
          val update = WaitForTake(ax, xs)
          if (stateRef.compareAndSet(current, update)) {
            asyncBoundary.map {
              notify(rightUnit)
              Some(value)
            }
          } else unsafeTryTake() // retry
        }
      }
      is WaitForPut -> justNone
    }

  private tailrec fun unsafeTake(onTake: Listener<A>): Kind<F, Unit> =
    when (val current = stateRef.value) {
      is State.WaitForTake -> {
        val value = current.value
        val queue = current.puts
        if (queue.isEmpty()) {
          if (stateRef.compareAndSet(current, State.empty())) {
            // Signals completion of `take`
            onTake(Either.Right(value))
            unit()
          } else {
            unsafeTake(onTake) // retry
          }
        } else {
          val (x, xs) = queue.dequeue()
          val (ax, awaitPut) = x
          val update = WaitForTake(ax, xs)
          if (stateRef.compareAndSet(current, update)) {
            // Complete the `put` request waiting on a notification
            asyncBoundary.map { _ ->
              try {
                awaitPut(rightUnit)
              } finally {
                onTake(Either.Right(value))
              }
            }
          } else {
            unsafeTake(onTake) // retry
          }
        }
      }

      is WaitForPut ->
        if (!stateRef.compareAndSet(current, WaitForPut(current.reads, current.takes.enqueue(onTake)))) {
          unsafeTake(onTake)
        } else unit()
    }

  private tailrec fun unsafeRead(onRead: Listener<A>): Unit =
    when (val current = stateRef.value) {
      is WaitForTake ->
        // A value is available, so complete `read` immediately without
        // changing the sate
        onRead(Right(current.value))

      is WaitForPut ->
        // No value available, enqueue the callback
        if (!stateRef.compareAndSet(current, WaitForPut(current.reads.enqueue(onRead), current.takes))) {
          unsafeRead(onRead) // retry
        } else Unit
    }

  private fun streamPutAndReads(a: A, reads: IQueue<Listener<A>>, first: Listener<A>?): Kind<F, Boolean> =
    asyncBoundary.map {
      val value = Right(a)
      reads.forEach { cb -> cb(value) } // Satisfies all current `read` requests found
      first?.invoke(value)
      true
    }

  private val justNone = just(None)
  private val justFalse = just(false)
  private val justTrue = just(true)
  private val asyncBoundary = async(unitCallback)

  companion object {
    /** Builds an [UncancelableMVar] instance with an [initial] value. */
    operator fun <F, A> invoke(initial: A, AS: Async<F>): Kind<F, MVar<F, A>> = AS.later {
      UncancelableMVar(State(initial), AS)
    }

    /** Returns an empty [UncancelableMVar] instance. */
    fun <F, A> empty(AS: Async<F>): Kind<F, MVar<F, A>> = AS.later {
      UncancelableMVar(State.empty<A>(), AS)
    }

    /** Internal state of [MVar]. */
    sealed class State<A> {

      companion object {
        @Suppress("UNCHECKED_CAST")
        fun <A> empty(): State<A> = EmptyState as State<A>

        operator fun <A> invoke(a: A): State<A> = WaitForTake(a, IQueue.empty())
      }

      /**
       * [UncancelableMVar] state signaling it has [take] callbacks registered
       * and we are waiting for one or multiple [put] operations.
       *
       * @param takes are the rest of the requests waiting in line,
       *        if more than one `take` requests were registered
       */
      class WaitForPut<A>(val reads: IQueue<Listener<A>>, val takes: IQueue<Listener<A>>) : State<A>()

      /**
       * [UncancelableMVar] state signaling it has one or more values enqueued,
       * to be signaled on the next [take].
       *
       * @param value is the first value to signal
       * @param puts are the rest of the `put` requests, along with the
       *        callbacks that need to be called whenever the corresponding
       *        value is first in line (i.e. when the corresponding `put`
       *        is unblocked from the user's point of view)
       */
      class WaitForTake<A>(val value: A, val puts: IQueue<Tuple2<A, Listener<Unit>>>) : State<A>()
    }
  }
}

private val EmptyState: WaitForPut<Any> = WaitForPut(IQueue.empty(), IQueue.empty())
private typealias Listener<A> = (Either<Nothing, A>) -> Unit
