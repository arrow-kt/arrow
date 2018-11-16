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
  fun complete(a: A): Kind<F, Boolean>

  /**
   * Completes the promise with the specified value.
   */
  fun error(throwable: Throwable): Kind<F, Boolean>

  companion object {
    fun <F, A> unsafeCancellable(AS: Async<F>): Promise<F, A> = CancellablePromise(AS, AtomicReference(CancellablePromise.State.Pending(emptyList())))
    fun <F, A> uncancelable(AS: Async<F>): Kind<F, Promise<F, A>> = AS { unsafeCancellable<F, A>(AS) }
  }

}

class CancellablePromise<F, A> internal constructor(private val AS: Async<F>,
                                                    private val state: AtomicReference<State<A>>): Promise<F, A> {

  override val get: Kind<F, A> = AS.async { k: (Either<Throwable, A>) -> Unit ->
    tailrec fun loop(): Unit {
      val st = state.get()
      when (st) {
        is State.Pending<A> -> loop()
        is State.Done -> k(Right(st.value))
        is State.Error -> k(Left(st.throwable))
      }
    }

    tailrec fun calculateNewState(): Unit {
      val oldState = state.get()
      val newState = when (oldState) {
        is State.Pending<A> -> State.Pending(oldState.joiners + k)
        is State.Done -> oldState
        is State.Error -> oldState
      }
      return if (state.compareAndSet(oldState, newState)) Unit else calculateNewState()
    }

    calculateNewState()
    loop()
  }

  override fun complete(a: A): Kind<F, Boolean> {
    tailrec fun calculateNewState(): Unit {
      val oldState = state.get()
      val newState = when (oldState) {
        is State.Pending<A> -> State.Done(a)
        is State.Done -> oldState
        is State.Error -> oldState
      }
      return if (state.compareAndSet(oldState, newState)) Unit else calculateNewState()
    }
    val oldState = state.get()
    return when (oldState) {
      is State.Pending -> calculateNewState().let { AS.just(true) }
      is State.Done -> AS.just(false)
      is State.Error -> AS.just(false)
    }
  }

  override fun error(throwable: Throwable): Kind<F, Boolean> = state.get().let {
    tailrec fun calculateNewState(): Unit {
      val oldState = state.get()
      val newState = when (oldState) {
        is State.Pending<A> -> State.Error(throwable)
        is State.Done -> oldState
        is State.Error -> oldState
      }
      return if (state.compareAndSet(oldState, newState)) Unit else calculateNewState()
    }

    val oldState = state.get()
    return when (oldState) {
      is State.Pending -> calculateNewState().let { AS.just(true) }
      is State.Done -> AS.just(false)
      is State.Error -> AS.just(false)
    }
  }

  internal sealed class State<out A> {
    data class Pending<A>(val joiners: List<(Either<Throwable, A>) -> Unit>) : State<A>()
    data class Done<A>(val value: A) : State<A>()
    data class Error<A>(val throwable: Throwable) : State<A>()
  }

}
