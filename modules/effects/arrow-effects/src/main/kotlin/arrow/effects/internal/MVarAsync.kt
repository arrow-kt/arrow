package arrow.effects.internal

import arrow.Kind
import arrow.core.*
import arrow.effects.MVar
import arrow.effects.typeclasses.Async
import java.util.concurrent.atomic.AtomicReference

/**
 * <<MVar>> implementation for <<Async>> data types.
 */
internal class MVarAsync<F, A> private constructor(initial: State<A>, AS: Async<F>) : MVar<F, A>, Async<F> by AS {

  /** Shared mutable state. */
  private val stateRef = AtomicReference<State<A>>(initial)

  override fun put(a: A): Kind<F, Unit> = async { cb ->
    unsafePut(a, cb)
  }

  override fun tryPut(a: A): Kind<F, Boolean> = async { cb ->
    unsafePut1(a, cb)
  }

  override fun take(): Kind<F, A> =
    async(::unsafeTake)

  override fun tryTake(): Kind<F, Option<A>> =
    async(::unsafeTake1)

  override fun read(): Kind<F, A> =
    async(::unsafeRead)

  override fun isEmpty(): Kind<F, Boolean> = delay {
    when (stateRef.get()) {
      is State.WaitForPut -> true
      is State.WaitForTake -> false
    }
  }

  override fun isNotEmpty(): Kind<F, Boolean> =
    isEmpty().map { !it }

  private tailrec fun unsafePut1(a: A, onPut: Listener<Boolean>): Unit =
    when (val current = stateRef.get()) {
      is State.WaitForTake -> onPut(Right(false))
      is State.WaitForPut -> {
        val first: Listener<A>? = current.takes.firstOrNull()
        val update: State<A> =
          if (current.takes.isEmpty()) State(a) else {
            val rest = current.takes.drop(1)
            if (rest.isEmpty()) State.empty()
            else State.WaitForPut(emptyList(), rest)
          }

        if (!stateRef.compareAndSet(current, update)) unsafePut1(a, onPut) // retry
        else {
          val value = Right(a)
          streamAll(value, current.reads) // Satisfies all current `read` requests found
          first?.invoke(value) // Satisfies the first `take` request found
          onPut(Right(true)) // Signals completion of `put`
        }
      }
    }

  private tailrec fun unsafePut(a: A, onPut: Listener<Unit>): Unit =
    when (val current = stateRef.get()) {
      is State.WaitForTake -> {
        val update = State.WaitForTake(current.value, current.puts + Tuple2(a, onPut))
        if (!stateRef.compareAndSet(current, update)) unsafePut(a, onPut) // retry
        else Unit
      }

      is State.WaitForPut -> {
        val first: Listener<A>? = current.takes.firstOrNull()
        val update: State<A> =
          if (current.takes.isEmpty()) State(a) else {
            val rest = current.takes.drop(1)
            if (rest.isEmpty()) State.empty()
            else State.WaitForPut(emptyList(), rest)
          }

        if (!stateRef.compareAndSet(current, update)) unsafePut(a, onPut) // retry
        else {
          val value = Right(a)
          streamAll(value, current.reads) // Satisfies all current `read` requests found
          first?.invoke(value)// Satisfies the first `take` request found
          onPut(Right(Unit)) // Signals completion of `put`
        }
      }
    }

  private tailrec fun unsafeTake1(onTake: Listener<Option<A>>): Unit =
    when (val current = stateRef.get()) {
      is State.WaitForTake ->
        if (current.puts.isEmpty()) {
          if (stateRef.compareAndSet(current, State.empty())) onTake(Right(Some(current.value))) // Signals completion of `take`
          else unsafeTake1(onTake) // retry
        } else {
          val (ax, notify) = current.puts.first()
          val xs = current.puts.drop(1)
          val update = State.WaitForTake(ax, xs)
          if (stateRef.compareAndSet(current, update)) {
            onTake(Right(Some(current.value))) // Signals completion of `take`
            notify(Right(Unit)) // Complete the `put` request waiting on a notification
          } else unsafeTake1(onTake) // retry
        }

      is State.WaitForPut -> onTake(Right(None))
    }

  private tailrec fun unsafeTake(onTake: Listener<A>): Unit =
    when (val current = stateRef.get()) {
      is State.WaitForTake ->
        if (current.puts.isEmpty()) {
          if (stateRef.compareAndSet(current, State.empty())) onTake(Right(current.value)) // Signals completion of `take`
          else unsafeTake(onTake) // retry }
        } else {
          val (ax, notify) = current.puts.first()
          val xs = current.puts.drop(1)
          val update = State.WaitForTake(ax, xs)
          if (stateRef.compareAndSet(current, update)) {
            onTake(Right(current.value)) // Signals completion of `take`
            notify(Right(Unit)) // Signals completion of `take`
          } else unsafeTake(onTake) // retry
        }

      is State.WaitForPut ->
        if (!stateRef.compareAndSet(current, State.WaitForPut(current.reads, current.takes + onTake))) unsafeTake(onTake)
        else Unit
    }

  private tailrec fun unsafeRead(onRead: Listener<A>): Unit =
    when (val current = stateRef.get()) {
      is State.WaitForTake ->
        // A value is available, so complete `read` immediately without
        // changing the sate
        onRead(Right(current.value))

      is State.WaitForPut ->
        // No value available, enqueue the callback
        if (!stateRef.compareAndSet(current, State.WaitForPut(current.reads + onRead, current.takes))) unsafeRead(onRead) // retry
        else Unit
    }

  private fun streamAll(value: Either<Nothing, A>, listeners: Iterable<Listener<A>>): Unit =
    listeners.forEach { it.invoke(value) }

  companion object {
    /** Builds an <<MVarAsync>> instance with an `initial` value. */
    operator fun <F, A> invoke(initial: A, AS: Async<F>): Kind<F, MVar<F, A>> = AS.delay {
      MVarAsync(State(initial), AS)
    }

    /** Returns an empty <<MVarAsync>> instance. */
    fun <F, A> empty(AS: Async<F>): Kind<F, MVar<F, A>> = AS.delay {
      MVarAsync(State.empty<A>(), AS)
    }

    /** ADT modelling the internal state of `MVar`. */
    private sealed class State<A> {

      /** Private <<State>> builders.*/
      companion object {
        private val ref = WaitForPut<Any>(emptyList(), emptyList())
        operator fun <A> invoke(a: A): State<A> = WaitForTake(a, emptyList())
        /** `Empty` state, reusing the same instance. */
        fun <A> empty(): State<A> = ref as State<A>
      }

      /**
       * `MVarAsync` state signaling it has `take` callbacks
       * registered and we are waiting for one or multiple
       * `put` operations.
       *
       * @param takes are the rest of the requests waiting in line,
       *        if more than one `take` requests were registered
       */
      data class WaitForPut<A>(val reads: List<Listener<A>>, val takes: List<Listener<A>>) : State<A>()

      /**
       * `MVarAsync` state signaling it has one or more values enqueued,
       * to be signaled on the next `take`.
       *
       * @param value is the first value to signal
       * @param puts are the rest of the `put` requests, along with the
       *        callbacks that need to be called whenever the corresponding
       *        value is first in line (i.e. when the corresponding `put`
       *        is unblocked from the user's point of view)
       */
      data class WaitForTake<A>(val value: A, val puts: List<Tuple2<A, Listener<Unit>>>) : State<A>()
    }

  }
}

/**
 * Internal API — Matches the callback type in `cats.effect.Async`,
 * but we don't care about the error.
 */
private typealias Listener<A> = (Either<Nothing, A>) -> Unit

