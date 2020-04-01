package arrow.fx.internal

import arrow.fx.IOResult
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic

class UnsafePromise<E, A> {

  private sealed class State<out E, out A> {
    object Empty : State<Nothing, Nothing>()
    data class Waiting<E, A>(val joiners: List<(IOResult<E, A>) -> Unit>) : State<E, A>()
    data class Full<E, A>(val a: IOResult<E, A>) : State<E, A>()
  }

  private val state: AtomicRef<State<E, A>> = atomic(State.Empty)

  fun get(cb: (IOResult<E, A>) -> Unit) {
    tailrec fun go(): Unit = when (val oldState = state.value) {
      State.Empty -> if (state.compareAndSet(oldState, State.Waiting(listOf(cb)))) Unit else go()
      is State.Waiting -> if (state.compareAndSet(oldState, State.Waiting(oldState.joiners + cb))) Unit else go()
      is State.Full -> cb(oldState.a)
    }

    go()
  }

  fun complete(value: IOResult<E, A>) {
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

  fun remove(cb: (IOResult<E, A>) -> Unit) = when (val oldState = state.value) {
    State.Empty -> Unit
    is State.Waiting -> state.value = State.Waiting(oldState.joiners - cb)
    is State.Full -> Unit
  }
}
