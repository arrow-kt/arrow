package arrow.data

import arrow.higherkind

@higherkind
data class Moore<E, V>(val view: V, val handle: (E) -> Moore<E, V>) : MooreOf<E, V>, MooreKindedJ<E, V> {

  fun <A> coflatMap(f: (Moore<E, V>) -> A): Moore<E, A> =
    Moore(f(Moore(view, handle))) { update -> handle(update).coflatMap(f) }

  fun <A> map(f: (V) -> A): Moore<E, A> =
    Moore(f(view)) { update -> handle(update).map(f) }

  fun extract(): V = view

  companion object
}
