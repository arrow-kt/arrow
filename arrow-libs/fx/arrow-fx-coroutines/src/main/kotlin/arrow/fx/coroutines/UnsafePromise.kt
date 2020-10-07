package arrow.fx.coroutines

import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic

/**
 * An eager Promise implementation to bridge results across processes internally.
 * @see ForkAndForget
 */
internal class UnsafePromise<A> {

  private sealed class State<out A> {
    object Empty : State<Nothing>()
    data class Waiting<A>(val joiners: List<(Result<A>) -> Unit>) : State<A>()

    @Suppress("RESULT_CLASS_IN_RETURN_TYPE")
    data class Full<A>(val a: Result<A>) : State<A>()
  }

  private val state: AtomicRef<State<A>> = atomic(State.Empty)

  @Suppress("RESULT_CLASS_IN_RETURN_TYPE")
  fun tryGet(): Result<A>? =
    when (val curr = state.value) {
      is State.Full -> curr.a
      else -> null
    }

  fun get(cb: (Result<A>) -> Unit) {
    tailrec fun go(): Unit = when (val oldState = state.value) {
      State.Empty -> if (state.compareAndSet(oldState, State.Waiting(listOf(cb)))) Unit else go()
      is State.Waiting -> if (state.compareAndSet(oldState, State.Waiting(oldState.joiners + cb))) Unit else go()
      is State.Full -> cb(oldState.a)
    }

    go()
  }

  suspend fun join(): A =
    cancellable { cb ->
      get(cb)
      CancelToken { remove(cb) }
    }

  fun complete(value: Result<A>) {
    tailrec fun go(): Unit = when (val oldState = state.value) {
      State.Empty -> if (state.compareAndSet(oldState, State.Full(value))) Unit else go()
      is State.Waiting -> {
        if (state.compareAndSet(oldState, State.Full(value))) oldState.joiners.forEach { it(value) }
        else go()
      }
      is State.Full -> throw ArrowInternalException("$ArrowExceptionMessage\nUnsafePromise completed twice")
    }

    go()
  }

  fun remove(cb: (Result<A>) -> Unit) = when (val oldState = state.value) {
    State.Empty -> Unit
    is State.Waiting -> state.value = State.Waiting(oldState.joiners - cb)
    is State.Full -> Unit
  }
}
