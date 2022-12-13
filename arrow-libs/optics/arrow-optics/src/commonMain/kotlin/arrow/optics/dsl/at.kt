package arrow.optics.dsl

import arrow.optics.Lens
import arrow.optics.Optional
import arrow.optics.Prism
import arrow.optics.Traversal
import arrow.optics.typeclasses.At

/**
 * DSL to compose [At] with a [Lens] for a structure [S] to focus in on [A] at given index [I].
 *
 * @receiver [Lens] with a focus in [S]
 * @param AT [At] instance to provide a [Lens] to zoom into [S] at [I]
 * @param i index [I] to zoom into [S] and find focus [A]
 * @return [Lens] with a focus in [A] at given index [I].
 */
public fun <T, S, I, A> Lens<T, S>.at(AT: At<S, I, A>, i: I): Lens<T, A> = this.compose(AT.at(i))

/**
 * DSL to compose [At] with a [Prism] for a structure [S] to focus in on [A] at given index [I].
 *
 * @receiver [Prism] with a focus in [S]
 * @param AT [At] instance to provide a [Lens] to zoom into [S] at [I]
 * @param i index [I] to zoom into [S] and find focus [A]
 * @return [Optional] with a focus in [A] at given index [I].
 */
public fun <T, S, I, A> Prism<T, S>.at(AT: At<S, I, A>, i: I): Optional<T, A> = this.compose(AT.at(i))

/**
 * DSL to compose [At] with an [Optional] for a structure [S] to focus in on [A] at given index [I].
 *
 * @receiver [Optional] with a focus in [S]
 * @param AT [At] instance to provide a [Lens] to zoom into [S] at [I]
 * @param i index [I] to zoom into [S] and find focus [A]
 * @return [Optional] with a focus in [A] at given index [I].
 */
public fun <T, S, I, A> Optional<T, S>.at(AT: At<S, I, A>, i: I): Optional<T, A> = this.compose(AT.at(i))

/**
 * DSL to compose [At] with a [Traversal] for a structure [S] to focus in on [A] at given index [I].
 *
 * @receiver [Traversal] with a focus in [S]
 * @param AT [At] instance to provide a [Lens] to zoom into [S] at [I]
 * @param i index [I] to zoom into [S] and find focus [A]
 * @return [Traversal] with a focus in [A] at given index [I].
 */
public fun <T, S, I, A> Traversal<T, S>.at(AT: At<S, I, A>, i: I): Traversal<T, A> = this.compose(AT.at(i))
