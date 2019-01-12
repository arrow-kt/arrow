package arrow.effects.internal

import arrow.core.Either
import java.util.concurrent.atomic.AtomicReference

internal class UnsafePromise<A> {

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