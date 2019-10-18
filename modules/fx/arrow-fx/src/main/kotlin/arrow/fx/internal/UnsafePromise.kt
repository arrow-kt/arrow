package arrow.fx.internal

import arrow.core.Either
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic

internal class UnsafePromise<A> {

  private sealed class State<out A> {
    object Empty : State<Nothing>()
    data class Waiting<A>(val joiners: List<(Either<Throwable, A>) -> Unit>) : State<A>()
    data class Full<A>(val a: Either<Throwable, A>) : State<A>()
  }

  private val state: AtomicRef<State<A>> = atomic(State.Empty)

  fun get(cb: (Either<Throwable, A>) -> Unit) {
    tailrec fun go(): Unit = when (val oldState = state.value) {
      State.Empty -> if (state.compareAndSet(oldState, State.Waiting(listOf(cb)))) Unit else go()
      is State.Waiting -> if (state.compareAndSet(oldState, State.Waiting(oldState.joiners + cb))) Unit else go()
      is State.Full -> cb(oldState.a)
    }

    go()
  }

  fun complete(value: Either<Throwable, A>) {
    tailrec fun go(): Unit = when (val oldState = state.value) {
      State.Empty -> if (state.compareAndSet(oldState, State.Full(value))) Unit else go()
      is State.Waiting -> {
        if (state.compareAndSet(oldState, State.Full(value))) oldState.joiners.forEach { it(value) }
        else go()
      }
      is State.Full -> throw ArrowInternalException()
    }

    go()
  }

  fun remove(cb: (Either<Throwable, A>) -> Unit) = when (val oldState = state.value) {
    State.Empty -> Unit
    is State.Waiting -> state.value = State.Waiting(oldState.joiners - cb)
    is State.Full -> Unit
  }
}
