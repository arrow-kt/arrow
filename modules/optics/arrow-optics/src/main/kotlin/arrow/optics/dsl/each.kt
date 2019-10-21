package arrow.optics.dsl

import arrow.optics.Fold
import arrow.optics.Iso
import arrow.optics.Lens
import arrow.optics.Optional
import arrow.optics.Prism
import arrow.optics.Setter
import arrow.optics.Traversal
import arrow.optics.typeclasses.Each

/**
 * DSL to compose [Each] with a [Lens] for a structure [S] to see all its foci [A]
 *
 * @receiver [Lens] with a focus in [S]
 * @param EA [Each] to provide [Traversal] that can focus into a structure [S] to see all its foci [A]
 * @return [Traversal] with a focus in [A]
 */
fun <T, S, A> Lens<T, S>.every(EA: Each<S, A>): Traversal<T, A> = this.compose(EA.each())

/**
 * DSL to compose [Each] with an [Iso] for a structure [S] to see all its foci [A]
 *
 * @receiver [Iso] with a focus in [S]
 * @param EA [Each] to provide [Traversal] that can focus into a structure [S] to see all its foci [A]
 * @return [Traversal] with a focus in [A]
 */
fun <T, S, A> Iso<T, S>.every(EA: Each<S, A>): Traversal<T, A> = this.compose(EA.each())

/**
 * DSL to compose [Each] with a [Prism] for a structure [S] to see all its foci [A]
 *
 * @receiver [Prism] with a focus in [S]
 * @param EA [Each] to provide [Traversal] that can focus into a structure [S] to see all its foci [A]
 * @return [Traversal] with a focus in [A]
 */
fun <T, S, A> Prism<T, S>.every(EA: Each<S, A>): Traversal<T, A> = this.compose(EA.each())

/**
 * DSL to compose [Each] with an [Optional] for a structure [S] to see all its foci [A]
 *
 * @receiver [Optional] with a focus in [S]
 * @param EA [Each] to provide [Traversal] that can focus into a structure [S] to see all its foci [A]
 * @return [Traversal] with a focus in [A]
 */
fun <T, S, A> Optional<T, S>.every(EA: Each<S, A>): Traversal<T, A> = this.compose(EA.each())

/**
 * DSL to compose [Each] with a [Setter] for a structure [S] to see all its foci [A]
 *
 * @receiver [Setter] with a focus in [S]
 * @param EA [Each] to provide [Traversal] that can focus into a structure [S] to see all its foci [A]
 * @return [Setter] with a focus in [A]
 */
fun <T, S, A> Setter<T, S>.every(EA: Each<S, A>): Setter<T, A> = this.compose(EA.each())

/**
 * DSL to compose [Each] with a [Traversal] for a structure [S] to see all its foci [A]
 *
 * @receiver [Traversal] with a focus in [S]
 * @param EA [Each] to provide [Traversal] that can focus into a structure [S] to see all its foci [A]
 * @return [Traversal] with a focus in [A]
 */
fun <T, S, A> Traversal<T, S>.every(EA: Each<S, A>): Traversal<T, A> = this.compose(EA.each())

/**
 * DSL to compose [Each] with a [Fold] for a structure [S] to see all its foci [A]
 *
 * @receiver [Fold] with a focus in [S]
 * @param EA [Each] to provide [Traversal] that can focus into a structure [S] to see all its foci [A]
 * @return [Fold] with a focus in [A]
 */
fun <T, S, A> Fold<T, S>.every(EA: Each<S, A>): Fold<T, A> = this.compose(EA.each())
