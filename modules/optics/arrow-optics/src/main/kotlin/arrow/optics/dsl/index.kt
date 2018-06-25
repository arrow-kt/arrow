package arrow.optics.dsl

import arrow.optics.*
import arrow.optics.typeclasses.Index

/**
 * DSL to compose [Index] with a [Lens] for a structure [S] to focus in on [A] at given index [I]
 *
 * @receiver [Lens] with a focus in [S]
 * @param ID [Index] instance to provide a [Optional] to focus into [S] at [I]
 * @param i index [I] to focus into [S] and find focus [A]
 * @return [Optional] with a focus in [A] at given index [I]
 */
fun <T, S, I, A> Lens<T, S>.index(ID: Index<S, I, A>, i: I): Optional<T, A> = this.compose(ID.index(i))

/**
 * DSL to compose [Index] with an [Iso] for a structure [S] to focus in on [A] at given index [I]
 *
 * @receiver [Iso] with a focus in [S]
 * @param ID [Index] instance to provide a [Optional] to focus into [S] at [I]
 * @param i index [I] to focus into [S] and find focus [A]
 * @return [Optional] with a focus in [A] at given index [I]
 */
fun <T, S, I, A> Iso<T, S>.index(ID: Index<S, I, A>, i: I): Optional<T, A> = this.compose(ID.index(i))

/**
 * DSL to compose [Index] with a [Prism] for a structure [S] to focus in on [A] at given index [I]
 *
 * @receiver [Prism] with a focus in [S]
 * @param ID [Index] instance to provide a [Optional] to focus into [S] at [I]
 * @param i index [I] to focus into [S] and find focus [A]
 * @return [Optional] with a focus in [A] at given index [I]
 */
fun <T, S, I, A> Prism<T, S>.index(ID: Index<S, I, A>, i: I): Optional<T, A> = this.compose(ID.index(i))

/**
 * DSL to compose [Index] with an [Optional] for a structure [S] to focus in on [A] at given index [I]
 *
 * @receiver [Optional] with a focus in [S]
 * @param ID [Index] instance to provide a [Optional] to focus into [S] at [I]
 * @param i index [I] to focus into [S] and find focus [A]
 * @return [Optional] with a focus in [A] at given index [I]
 */
fun <T, S, I, A> Optional<T, S>.index(ID: Index<S, I, A>, i: I): Optional<T, A> = this.compose(ID.index(i))

/**
 * DSL to compose [Index] with a [Setter] for a structure [S] to focus in on [A] at given index [I]
 *
 * @receiver [Setter] with a focus in [S]
 * @param ID [Index] instance to provide a [Optional] to focus into [S] at [I]
 * @param i index [I] to focus into [S] and find focus [A]
 * @return [Setter] with a focus in [A] at given index [I].
 */
fun <T, S, I, A> Setter<T, S>.index(ID: Index<S, I, A>, i: I): Setter<T, A> = this.compose(ID.index(i))

/**
 * DSL to compose [Index] with a [Traversal] for a structure [S] to focus in on [A] at given index [I]
 *
 * @receiver [Traversal] with a focus in [S]
 * @param ID [Index] instance to provide a [Optional] to focus into [S] at [I]
 * @param i index [I] to focus into [S] and find focus [A]
 * @return [Traversal] with a focus in [A] at given index [I].
 */
fun <T, S, I, A> Traversal<T, S>.index(ID: Index<S, I, A>, i: I): Traversal<T, A> = this.compose(ID.index(i))

/**
 * DSL to compose [Index] with a [Fold] for a structure [S] to focus in on [A] at given index [I]
 *
 * @receiver [Fold] with a focus in [S]
 * @param ID [Index] instance to provide a [Optional] to focus into [S] at [I]
 * @param i index [I] to focus into [S] and find focus [A]
 * @return [Fold] with a focus in [A] at given index [I].
 */
fun <T, S, I, A> Fold<T, S>.index(ID: Index<S, I, A>, i: I): Fold<T, A> = this.compose(ID.index(i))
