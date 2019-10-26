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
internal class UncancelableMVar<F, A> private constructor(initial: State<A>, private val AS: Async<F>) : MVar<F, A>, Async<F> by AS {

  private val stateRef = atomic<State<A>>(initial)

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
      is State.WaitForTake -> justFalse
      is State.WaitForPut -> {
        val first: ((Either<Throwable, A>) -> Unit)? = current.takes.firstOrNull()
        val update: State<A> =
          if (current.takes.isEmpty()) State(a) else {
            val rest = current.takes.drop(1)
            if (rest.isEmpty()) State.empty()
            else State.WaitForPut(emptyList(), rest)
          }

        if (!stateRef.compareAndSet(current, update)) unsafeTryPut(a) // retry
        else if (first != null && current.reads.isNotEmpty()) streamPutAndReads(a, current.reads, first)
        else justTrue
      }
    }

  private tailrec fun unsafePut(a: A, onPut: (Either<Throwable, Unit>) -> Unit): Kind<F, Unit> =
    when (val current = stateRef.value) {
      is State.WaitForTake -> {
        val update = State.WaitForTake(current.value, current.puts + Tuple2(a, onPut))
        if (!stateRef.compareAndSet(current, update)) unsafePut(a, onPut) // retry
        else unit()
      }

      is State.WaitForPut -> {
        val first: ((Either<Throwable, A>) -> Unit)? = current.takes.firstOrNull()
        val update: State<A> =
          if (current.takes.isEmpty()) State(a) else {
            val rest = current.takes.drop(1)
            if (rest.isEmpty()) State.empty()
            else State.WaitForPut(emptyList(), rest)
          }

        if (!stateRef.compareAndSet(current, update)) unsafePut(a, onPut) // retry
        else streamPutAndReads(a, current.reads, first).map { onPut(rightUnit) }
      }
    }

  private tailrec fun unsafeTryTake(): Kind<F, Option<A>> =
    when (val current = stateRef.value) {
      is State.WaitForTake ->
        if (current.puts.isEmpty()) {
          if (stateRef.compareAndSet(current, State.empty())) just(Some(current.value)) // Signals completion of `take`
          else unsafeTryTake() // retry
        } else {
          val (ax, notify) = current.puts.first()
          val xs = current.puts.drop(1)
          val update = State.WaitForTake(ax, xs)
          if (stateRef.compareAndSet(current, update)) {
            asyncBoundary.map {
              notify(rightUnit) // Complete the `put` request waiting on a notification
              Some(current.value) // Signals completion of `take`
            }
          } else unsafeTryTake() // retry
        }

      is State.WaitForPut -> justNone
    }

  private tailrec fun unsafeTake(onTake: (Either<Throwable, A>) -> Unit): Kind<F, Unit> =
    when (val current = stateRef.value) {
      is State.WaitForTake ->
        if (current.puts.isEmpty()) {
          if (stateRef.compareAndSet(current, State.empty())) {
            onTake(Right(current.value))
            unit() // Signals completion of `take`
          } else unsafeTake(onTake) // retry
        } else {
          val (ax, notify) = current.puts.first()
          val xs = current.puts.drop(1)
          val update = State.WaitForTake(ax, xs)
          if (stateRef.compareAndSet(current, update)) {
            asyncBoundary.map {
              try {
                notify(rightUnit)
              } // Signals completion of `take`
              finally {
                onTake(Right(current.value))
              } // Signals completion of `take`
            }
          } else unsafeTake(onTake) // retry
        }

      is State.WaitForPut ->
        if (!stateRef.compareAndSet(current, State.WaitForPut(current.reads, current.takes + onTake))) unsafeTake(onTake)
        else unit()
    }

  private tailrec fun unsafeRead(onRead: (Either<Throwable, A>) -> Unit): Unit =
    when (val current = stateRef.value) {
      is State.WaitForTake ->
        // A value is available, so complete `read` immediately without
        // changing the sate
        onRead(Right(current.value))

      is State.WaitForPut ->
        // No value available, enqueue the callback
        if (!stateRef.compareAndSet(current, State.WaitForPut(current.reads + onRead, current.takes))) unsafeRead(onRead) // retry
        else Unit
    }

  private fun streamPutAndReads(a: A, reads: List<(Either<Throwable, A>) -> Unit>, first: ((Either<Throwable, A>) -> Unit)?): Kind<F, Boolean> =
    asyncBoundary.map {
      val value = Right(a)
      reads.forEach { cb -> cb(value) } // Satisfies all current `read` requests found
      first?.invoke(value)
      true
    }

  override fun <A, B> Kind<F, A>.ap(ff: Kind<F, (A) -> B>): Kind<F, B> = AS.run {
    this@ap.ap(ff)
  }

  override fun <A, B> Kind<F, A>.map(f: (A) -> B): Kind<F, B> = AS.run {
    this@map.map(f)
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
    private sealed class State<A> {

      companion object {
        operator fun <A> invoke(a: A): State<A> = WaitForTake(a, emptyList())
        private val ref = WaitForPut<Any>(emptyList(), emptyList())
        @Suppress("UNCHECKED_CAST")
        fun <A> empty(): State<A> = ref as State<A>
      }

      /**
       * [UncancelableMVar] state signaling it has [take] callbacks registered
       * and we are waiting for one or multiple [put] operations.
       *
       * @param takes are the rest of the requests waiting in line,
       *        if more than one `take` requests were registered
       */
      data class WaitForPut<A>(val reads: List<(Either<Throwable, A>) -> Unit>, val takes: List<(Either<Throwable, A>) -> Unit>) : State<A>()

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
      data class WaitForTake<A>(val value: A, val puts: List<Tuple2<A, (Either<Throwable, Unit>) -> Unit>>) : State<A>()
    }
  }
}
