package arrow.ui

import arrow.core.identity
import arrow.higherkind

@higherkind
data class Store<S, V>(val state: S, val render: (S) -> V) : StoreOf<S, V>, StoreKindedJ<S, V> {
  fun <A> map(f: (V) -> A): Store<S, A> =
    Store(state) { state -> f(render(state)) }

  fun <A> coflatMap(f: (Store<S, V>) -> A): Store<S, A> =
    Store(state) { next: S -> f(Store(next, render)) }

  fun extract(): V = render(state)

  fun duplicate(): Store<S, Store<S, V>> = coflatMap(::identity)

  fun move(newState: S): Store<S, V> = duplicate().render(newState)

  companion object
}
