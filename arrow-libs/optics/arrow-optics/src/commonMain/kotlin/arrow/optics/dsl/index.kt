package arrow.optics.dsl

import arrow.optics.Lens
import arrow.optics.Optional
import arrow.optics.Prism
import arrow.optics.Traversal
import arrow.optics.typeclasses.Index

/**
 * DSL to compose [Index] with a [Lens] for a structure [S] to focus in on [A] at given index [I]
 *
 * @receiver [Lens] with a focus in [S]
 * @param idx [Index] instance to provide a [Optional] to focus into [S] at [I]
 * @param i index [I] to focus into [S] and find focus [A]
 * @return [Optional] with a focus in [A] at given index [I]
 */
public fun <T, S, I, A> Lens<T, S>.index(idx: Index<S, I, A>, i: I): Optional<T, A> = this.compose(idx.index(i))

/**
 * DSL to compose [Index] with an [Optional] for a structure [S] to focus in on [A] at given index [I]
 *
 * @receiver [Optional] or [Prism] with a focus in [S]
 * @param idx [Index] instance to provide a [Optional] to focus into [S] at [I]
 * @param i index [I] to focus into [S] and find focus [A]
 * @return [Optional] with a focus in [A] at given index [I]
 */
public fun <T, S, I, A> Optional<T, S>.index(idx: Index<S, I, A>, i: I): Optional<T, A> = this.compose(idx.index(i))

/**
 * DSL to compose [Index] with a [Traversal] for a structure [S] to focus in on [A] at given index [I]
 *
 * @receiver [Traversal] with a focus in [S]
 * @param idx [Index] instance to provide a [Optional] to focus into [S] at [I]
 * @param i index [I] to focus into [S] and find focus [A]
 * @return [Traversal] with a focus in [A] at given index [I].
 */
public fun <T, S, I, A> Traversal<T, S>.index(idx: Index<S, I, A>, i: I): Traversal<T, A> = this.compose(idx.index(i))
