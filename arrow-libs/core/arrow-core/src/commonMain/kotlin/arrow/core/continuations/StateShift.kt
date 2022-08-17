@file:OptIn(ExperimentalTypeInference::class)

package arrow.core.continuations

import arrow.atomic.Atomic
import kotlin.experimental.ExperimentalTypeInference

/**
 * Intersection of Atomic<State> & Shift<R>.
 * Will be replaced by `context(Atomic<State>, Shift<R>)` later
 */
public interface StateShift<State, R> : Atomic<State>, Shift<R>

public typealias StateEffect<State, R, A> = suspend StateShift<State, R>.() -> A

public typealias EagerStateEffect<State, R, A> = StateShift<State, R>.() -> A

public inline fun <State, R, A, B> fold(
  initial: State,
  @BuilderInference program: StateShift<State, R>.() -> A,
  error: (state: State, error: Throwable) -> B,
  recover: (state: State, shifted: R) -> B,
  transform: (state: State, value: A) -> B,
): B {
  val state = Atomic(initial)
  return fold(
    { program(DefaultStateShift(state, this)) },
    { error(state.value, it) },
    { recover(state.value, it) },
    { transform(state.value, it) }
  )
}

/** Default intersection boilerplate. PublishedApi to support _inline_ */
@PublishedApi
internal class DefaultStateShift<State, R>(
  state: Atomic<State>,
  shift: Shift<R>,
) : StateShift<State, R>, Atomic<State> by state, Shift<R> by shift {}
