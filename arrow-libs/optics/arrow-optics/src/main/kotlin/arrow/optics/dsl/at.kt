package arrow.optics.dsl

import arrow.optics.Fold
import arrow.optics.Getter
import arrow.optics.Iso
import arrow.optics.Lens
import arrow.optics.Optional
import arrow.optics.Prism
import arrow.optics.Setter
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
fun <T, S, I, A> Lens<T, S>.at(AT: At<S, I, A>, i: I): Lens<T, A> = this.compose(AT.at(i))

/**
 * DSL to compose [At] with an [Iso] for a structure [S] to focus in on [A] at given index [I].
 *
 * @receiver [Iso] with a focus in [S]
 * @param AT [At] instance to provide a [Lens] to zoom into [S] at [I]
 * @param i index [I] to zoom into [S] and find focus [A]
 * @return [Lens] with a focus in [A] at given index [I].
 */
fun <T, S, I, A> Iso<T, S>.at(AT: At<S, I, A>, i: I): Lens<T, A> = this.compose(AT.at(i))

/**
 * DSL to compose [At] with a [Prism] for a structure [S] to focus in on [A] at given index [I].
 *
 * @receiver [Prism] with a focus in [S]
 * @param AT [At] instance to provide a [Lens] to zoom into [S] at [I]
 * @param i index [I] to zoom into [S] and find focus [A]
 * @return [Optional] with a focus in [A] at given index [I].
 */
fun <T, S, I, A> Prism<T, S>.at(AT: At<S, I, A>, i: I): Optional<T, A> = this.compose(AT.at(i))

/**
 * DSL to compose [At] with an [Optional] for a structure [S] to focus in on [A] at given index [I].
 *
 * @receiver [Optional] with a focus in [S]
 * @param AT [At] instance to provide a [Lens] to zoom into [S] at [I]
 * @param i index [I] to zoom into [S] and find focus [A]
 * @return [Optional] with a focus in [A] at given index [I].
 */
fun <T, S, I, A> Optional<T, S>.at(AT: At<S, I, A>, i: I): Optional<T, A> = this.compose(AT.at(i))

/**
 * DSL to compose [At] with a [Getter] for a structure [S] to focus in on [A] at given index [I].
 *
 * @receiver [Getter] with a focus in [S]
 * @param AT [At] instance to provide a [Lens] to zoom into [S] at [I]
 * @param i index [I] to zoom into [S] and find focus [A]
 * @return [Getter] with a focus in [A] at given index [I].
 */
fun <T, S, I, A> Getter<T, S>.at(AT: At<S, I, A>, i: I): Getter<T, A> = this.compose(AT.at(i))

/**
 * DSL to compose [At] with a [Setter] for a structure [S] to focus in on [A] at given index [I].
 *
 * @receiver [Setter] with a focus in [S]
 * @param AT [At] instance to provide a [Lens] to zoom into [S] at [I]
 * @param i index [I] to zoom into [S] and find focus [A]
 * @return [Setter] with a focus in [A] at given index [I].
 */
fun <T, S, I, A> Setter<T, S>.at(AT: At<S, I, A>, i: I): Setter<T, A> = this.compose(AT.at(i))

/**
 * DSL to compose [At] with a [Traversal] for a structure [S] to focus in on [A] at given index [I].
 *
 * @receiver [Traversal] with a focus in [S]
 * @param AT [At] instance to provide a [Lens] to zoom into [S] at [I]
 * @param i index [I] to zoom into [S] and find focus [A]
 * @return [Traversal] with a focus in [A] at given index [I].
 */
fun <T, S, I, A> Traversal<T, S>.at(AT: At<S, I, A>, i: I): Traversal<T, A> = this.compose(AT.at(i))

/**
 * DSL to compose [At] with a [Fold] for a structure [S] to focus in on [A] at given index [I].
 *
 * @receiver [Fold] with a focus in [S]
 * @param AT [At] instance to provide a [Lens] to zoom into [S] at [I]
 * @param i index [I] to zoom into [S] and find focus [A]
 * @return [Fold] with a focus in [A] at given index [I].
 */
fun <T, S, I, A> Fold<T, S>.at(AT: At<S, I, A>, i: I): Fold<T, A> = this.compose(AT.at(i))
