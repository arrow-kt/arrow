@file:OptIn(ExperimentalTypeInference::class)

package arrow.core.continuations

import arrow.atomic.Atomic
import arrow.atomic.value
import kotlin.experimental.ExperimentalTypeInference

/**
 * Intersection of Atomic<State> & Raise<R>.
 * Will be replaced by `context(Atomic<State>, Raise<R>)` later
 */
public interface StateRaise<State, R> : Raise<R> {
  public val state: Atomic<State>
}

public typealias StateEffect<State, R, A> = suspend StateRaise<State, R>.() -> A

public typealias EagerStateEffect<State, R, A> = StateRaise<State, R>.() -> A

public inline fun <State, R, A, B> fold(
  initial: State,
  @BuilderInference program: StateRaise<State, R>.() -> A,
  error: (state: State, error: Throwable) -> B,
  recover: (state: State, raised: R) -> B,
  transform: (state: State, value: A) -> B,
): B {
  val state = Atomic(initial)
  return fold(
    { program(DefaultStateRaise(state, this)) },
    { error(state.value, it) },
    { recover(state.value, it) },
    { transform(state.value, it) }
  )
}

/** Default intersection boilerplate. PublishedApi to support _inline_ */
@PublishedApi
internal class DefaultStateRaise<State, R>(
  override val state: Atomic<State>,
  raise: Raise<R>,
) : StateRaise<State, R>, Raise<R> by raise {}
