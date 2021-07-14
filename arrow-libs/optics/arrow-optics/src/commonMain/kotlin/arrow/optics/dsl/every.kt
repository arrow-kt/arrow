package arrow.optics.dsl

import arrow.optics.Every
import arrow.optics.Fold
import arrow.optics.Iso
import arrow.optics.Lens
import arrow.optics.Optional
import arrow.optics.Prism
import arrow.optics.Setter
import arrow.optics.Traversal

/**
 * DSL to compose [Traversal] with a [Lens] for a structure [S] to see all its foci [A]
 *
 * @receiver [Lens] with a focus in [S]
 * @param TR [Traversal] that can focus into a structure [S] to see all its foci [A]
 * @return [Traversal] with a focus in [A]
 */
public fun <T, S, A> Lens<T, S>.every(TR: Every<S, A>): Every<T, A> = this.compose(TR)

/**
 * DSL to compose [Traversal] with an [Iso] for a structure [S] to see all its foci [A]
 *
 * @receiver [Iso] with a focus in [S]
 * @param TR [Traversal] that can focus into a structure [S] to see all its foci [A]
 * @return [Traversal] with a focus in [A]
 */
public fun <T, S, A> Iso<T, S>.every(TR: Every<S, A>): Every<T, A> = this.compose(TR)

/**
 * DSL to compose [Traversal] with a [Prism] for a structure [S] to see all its foci [A]
 *
 * @receiver [Prism] with a focus in [S]
 * @param TR [Traversal] that can focus into a structure [S] to see all its foci [A]
 * @return [Traversal] with a focus in [A]
 */
public fun <T, S, A> Prism<T, S>.every(TR: Every<S, A>): Every<T, A> = this.compose(TR)

/**
 * DSL to compose [Traversal] with an [Optional] for a structure [S] to see all its foci [A]
 *
 * @receiver [Optional] with a focus in [S]
 * @param TR [Traversal] that can focus into a structure [S] to see all its foci [A]
 * @return [Traversal] with a focus in [A]
 */
public fun <T, S, A> Optional<T, S>.every(TR: Every<S, A>): Every<T, A> = this.compose(TR)

/**
 * DSL to compose [Traversal] with a [Setter] for a structure [S] to see all its foci [A]
 *
 * @receiver [Setter] with a focus in [S]
 * @param TR [Traversal] that can focus into a structure [S] to see all its foci [A]
 * @return [Setter] with a focus in [A]
 */
public fun <T, S, A> Setter<T, S>.every(TR: Every<S, A>): Setter<T, A> = this.compose(TR)

/**
 * DSL to compose [Traversal] with a [Traversal] for a structure [S] to see all its foci [A]
 *
 * @receiver [Traversal] with a focus in [S]
 * @param TR [Traversal] that can focus into a structure [S] to see all its foci [A]
 * @return [Traversal] with a focus in [A]
 */
public fun <T, S, A> Traversal<T, S>.every(TR: Every<S, A>): Traversal<T, A> = this.compose(TR)

/**
 * DSL to compose [Traversal] with a [Fold] for a structure [S] to see all its foci [A]
 *
 * @receiver [Fold] with a focus in [S]
 * @param TR [Traversal] that can focus into a structure [S] to see all its foci [A]
 * @return [Fold] with a focus in [A]
 */
public fun <T, S, A> Fold<T, S>.every(TR: Every<S, A>): Fold<T, A> = this.compose(TR)
