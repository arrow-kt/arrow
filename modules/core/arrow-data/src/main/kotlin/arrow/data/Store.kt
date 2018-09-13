package arrow.data

import arrow.higherkind

@higherkind
data class Store<S, V>(val state: S, val render: (S) -> V) : StoreOf<S, V> {
  fun <A> map(f: (V) -> A): Store<S, A> =
      Store(state) { state -> f(render(state)) }

  fun <A> extend(f: (Store<S, V>) -> A): Store<S, A> =
      Store(state) { next: S -> f(Store(next, render)) }

  fun extract(): V = render(state)

  companion object
}
