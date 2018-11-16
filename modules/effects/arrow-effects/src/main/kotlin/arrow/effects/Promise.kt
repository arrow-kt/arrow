package arrow.effects

import arrow.Kind
import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import arrow.effects.typeclasses.Async
import java.util.concurrent.atomic.AtomicReference

/**
 * Needs to be build with `Concurrent`, if `F` is cancellable than `get` becomes unsafe..
 */
class Promise<F, A> private constructor(private val AS: Async<F>,
                                        private val state: AtomicReference<State<A>>) {

  companion object {
    fun <F, A> unsafe(AS: Async<F>): Promise<F, A> = Promise(AS, AtomicReference(State.Pending(emptyList())))
    fun <F, A> make(AS: Async<F>): Kind<F, Promise<F, A>> = AS { unsafe<F, A>(AS) }
  }
  /**
   * Retrieves the value of the promise, suspending the fiber running the action
   * until the result is available.
   */
  val get: Kind<F, A> = AS.async { k: (Either<Throwable, A>) -> Unit ->
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
  /**
   * Completes the promise with the specified value.
   */
  fun complete(a: A): Kind<F, Boolean> {
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
  /**
   * Completes the promise with the specified value.
   */
  fun error(throwable: Throwable): Kind<F, Boolean> = state.get().let {
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
      is State.Pending -> calculateNewState().let { _ -> AS.just(true) }
      is State.Done -> AS.just(false)
      is State.Error -> AS.just(false)
    }
  }

  sealed class State<out A> {
    data class Pending<A>(val joiners: List<(Either<Throwable, A>) -> Unit>) : State<A>()
    data class Done<A>(val value: A) : State<A>()
    data class Error<A>(val throwable: Throwable) : State<A>()
  }

}