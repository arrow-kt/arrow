package arrow.optics.dsl

import arrow.optics.Traversal

/**
 * DSL to compose [Traversal] with a [Traversal] for a structure [S] to see all its foci [A]
 *
 * @receiver [Traversal] with a focus in [S]
 * @param tr [Traversal] that can focus into a structure [S] to see all its foci [A]
 * @return [Traversal] with a focus in [A]
 */
public fun <T, S, A> Traversal<T, S>.every(tr: Traversal<S, A>): Traversal<T, A> = this.compose(tr)
