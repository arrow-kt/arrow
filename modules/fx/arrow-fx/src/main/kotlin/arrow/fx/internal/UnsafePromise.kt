package arrow.fx.internal

import arrow.core.Either
import java.util.concurrent.atomic.AtomicReference

internal class UnsafePromise<E, A> {

  private sealed class State<E, out A> {
    class Empty<E, A> : State<E, A>()
    data class Waiting<E, A>(val joiners: List<(Either<E, A>) -> Unit>) : State<E, A>()
    data class Full<E, A>(val a: Either<E, A>) : State<E, A>()
  }

  private val state: AtomicReference<State<E, A>> = AtomicReference(State.Empty())

  fun get(cb: (Either<E, A>) -> Unit) {
    tailrec fun go(): Unit = when (val oldState = state.get()) {
      is State.Empty -> if (state.compareAndSet(oldState, State.Waiting(listOf(cb)))) Unit else go()
      is State.Waiting -> if (state.compareAndSet(oldState, State.Waiting(oldState.joiners + cb))) Unit else go()
      is State.Full -> cb(oldState.a)
    }

    go()
  }

  fun complete(value: Either<E, A>) {
    tailrec fun go(): Unit = when (val oldState = state.get()) {
      is State.Empty -> if (state.compareAndSet(oldState, State.Full(value))) Unit else go()
      is State.Waiting -> {
        if (state.compareAndSet(oldState, State.Full(value))) oldState.joiners.forEach { it(value) }
        else go()
      }
      is State.Full -> throw ArrowInternalException()
    }

    go()
  }

  fun remove(cb: (Either<E, A>) -> Unit) = when (val oldState = state.get()) {
    is State.Empty -> Unit
    is State.Waiting -> state.set(State.Waiting(oldState.joiners - cb))
    is State.Full -> Unit
  }
}
