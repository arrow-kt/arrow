package arrow.optics.dsl

import arrow.core.NonEmptyList
import arrow.optics.Lens
import arrow.optics.Optional
import arrow.optics.Prism
import arrow.optics.Traversal
import arrow.optics.typeclasses.Index
import kotlin.jvm.JvmName

/**
 * DSL to compose [Index] with an [Optional] for a structure [S] to focus in on [A] at given index [I]
 *
 * @receiver [Optional], [Lens], or [Prism] with a focus in [S]
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

public fun <T, A> Optional<T, List<A>>.index(index: Int): Optional<T, A> =
  this.compose(Index.list<A>().index(index))

public fun <T, A> Traversal<T, List<A>>.index(index: Int): Traversal<T, A> =
  this.compose(Index.list<A>().index(index))

@JvmName("indexNonEmptyList")
public fun <T, A> Optional<T, NonEmptyList<A>>.index(index: Int): Optional<T, A> =
  this.compose(Index.nonEmptyList<A>().index(index))

@JvmName("indexNonEmptyList")
public fun <T, A> Traversal<T, NonEmptyList<A>>.index(index: Int): Traversal<T, A> =
  this.compose(Index.nonEmptyList<A>().index(index))

@JvmName("indexValues")
public fun <T, K, A> Optional<T, Map<K, A>>.index(key: K): Optional<T, A> =
  this.compose(Index.map<K, A>().index(key))

@JvmName("indexValues")
public fun <T, K, A> Traversal<T, Map<K, A>>.index(key: K): Traversal<T, A> =
  this.compose(Index.map<K, A>().index(key))

@JvmName("indexSequence")
public fun <T, A> Optional<T, Sequence<A>>.index(index: Int): Optional<T, A> =
  this.compose(Index.sequence<A>().index(index))

@JvmName("indexSequence")
public fun <T, A> Traversal<T, Sequence<A>>.index(index: Int): Traversal<T, A> =
  this.compose(Index.sequence<A>().index(index))

@JvmName("indexString")
public fun <T> Optional<T, String>.index(index: Int): Optional<T, Char> =
  this.compose(Index.string().index(index))

@JvmName("indexString")
public fun <T> Traversal<T, String>.index(index: Int): Traversal<T, Char> =
  this.compose(Index.string().index(index))
