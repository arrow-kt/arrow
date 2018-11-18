package arrow.effects

import arrow.Kind
import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import arrow.effects.typeclasses.Async
import java.util.concurrent.atomic.AtomicReference

interface Promise<F, A> {

  /**
   * Retrieves the value of the promise,
   * suspending the fiber running the action until the result is available.
   */
  val get: Kind<F, A>

  /**
   * Completes the promise with the specified value.
   */
  fun complete(a: A): Kind<F, Unit>

  /**
   * Completes the promise with the specified value.
   */
  fun error(throwable: Throwable): Kind<F, Unit>

  companion object {
    fun <F, A> unsafeCancellable(AS: Async<F>): Promise<F, A> = CancellablePromise(AS, AtomicReference(CancellablePromise.State.Pending(emptyList())))
    fun <F, A> uncancelable(AS: Async<F>): Kind<F, Promise<F, A>> = AS { unsafeCancellable<F, A>(AS) }
  }

  object AlreadyFulfilled: Throwable(message = "Promise was already fulfilled")

}

class CancellablePromise<F, A> internal constructor(private val AS: Async<F>,
                                                    private val state: AtomicReference<State<A>>) : Promise<F, A> {

  override val get: Kind<F, A> = AS.async { k: (Either<Throwable, A>) -> Unit ->
    tailrec fun loop(): Unit {
      val st = state.get()
      when (st) {
        is State.Pending<A> -> loop()
        is State.Fulfilled -> k(Right(st.value))
        is State.Error -> k(Left(st.throwable))
      }
    }

    tailrec fun calculateNewState(): Unit {
      val oldState = state.get()
      val newState = when (oldState) {
        is State.Pending<A> -> State.Pending(oldState.joiners + k)
        is State.Fulfilled -> oldState
        is State.Error -> oldState
      }
      return if (state.compareAndSet(oldState, newState)) Unit else calculateNewState()
    }

    calculateNewState()
    loop()
  }

  override fun complete(a: A): Kind<F, Unit> {
    tailrec fun calculateNewState(): Unit {
      val oldState = state.get()
      val newState = when (oldState) {
        is State.Pending<A> -> State.Fulfilled(a)
        is State.Fulfilled -> oldState
        is State.Error -> oldState
      }
      return if (state.compareAndSet(oldState, newState)) Unit else calculateNewState()
    }
    val oldState = state.get()
    return when (oldState) {
      is State.Pending -> calculateNewState().let { AS.just(Unit) }
      is State.Fulfilled -> AS.raiseError(Promise.AlreadyFulfilled)
      is State.Error -> AS.raiseError(Promise.AlreadyFulfilled)
    }
  }

  override fun error(throwable: Throwable): Kind<F, Unit> = state.get().let { oldState ->
    when (oldState) {
      is State.Pending -> AS.raiseError(throwable)
      is State.Fulfilled -> AS.raiseError(Promise.AlreadyFulfilled)
      is State.Error -> AS.raiseError(Promise.AlreadyFulfilled)
    }
  }

  internal sealed class State<out A> {
    data class Pending<A>(val joiners: List<(Either<Throwable, A>) -> Unit>) : State<A>()
    data class Fulfilled<A>(val value: A) : State<A>()
    data class Error<A>(val throwable: Throwable) : State<A>()
  }

}
