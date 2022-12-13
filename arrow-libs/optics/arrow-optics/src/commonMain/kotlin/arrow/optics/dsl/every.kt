package arrow.optics.dsl

import arrow.optics.Lens
import arrow.optics.Optional
import arrow.optics.Prism
import arrow.optics.Traversal

/**
 * DSL to compose [Traversal] with a [Lens] for a structure [S] to see all its foci [A]
 *
 * @receiver [Lens] with a focus in [S]
 * @param TR [Traversal] that can focus into a structure [S] to see all its foci [A]
 * @return [Traversal] with a focus in [A]
 */
public fun <T, S, A> Lens<T, S>.every(TR: Traversal<S, A>): Traversal<T, A> = this.compose(TR)

/**
 * DSL to compose [Traversal] with a [Prism] for a structure [S] to see all its foci [A]
 *
 * @receiver [Prism] with a focus in [S]
 * @param TR [Traversal] that can focus into a structure [S] to see all its foci [A]
 * @return [Traversal] with a focus in [A]
 */
public fun <T, S, A> Prism<T, S>.every(TR: Traversal<S, A>): Traversal<T, A> = this.compose(TR)

/**
 * DSL to compose [Traversal] with an [Optional] for a structure [S] to see all its foci [A]
 *
 * @receiver [Optional] with a focus in [S]
 * @param TR [Traversal] that can focus into a structure [S] to see all its foci [A]
 * @return [Traversal] with a focus in [A]
 */
public fun <T, S, A> Optional<T, S>.every(TR: Traversal<S, A>): Traversal<T, A> = this.compose(TR)

/**
 * DSL to compose [Traversal] with a [Traversal] for a structure [S] to see all its foci [A]
 *
 * @receiver [Traversal] with a focus in [S]
 * @param TR [Traversal] that can focus into a structure [S] to see all its foci [A]
 * @return [Traversal] with a focus in [A]
 */
public fun <T, S, A> Traversal<T, S>.every(TR: Traversal<S, A>): Traversal<T, A> = this.compose(TR)
