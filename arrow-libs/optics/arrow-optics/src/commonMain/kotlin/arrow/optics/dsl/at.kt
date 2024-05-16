package arrow.optics.dsl

import arrow.core.Option
import arrow.optics.Lens
import arrow.optics.Optional
import arrow.optics.Prism
import arrow.optics.Traversal
import arrow.optics.typeclasses.At
import kotlin.jvm.JvmName

/**
 * DSL to compose [At] with a [Lens] for a structure [S] to focus in on [A] at given index [I].
 *
 * @receiver [Lens] with a focus in [S]
 * @param at [At] instance to provide a [Lens] to zoom into [S] at [I]
 * @param i index [I] to zoom into [S] and find focus [A]
 * @return [Lens] with a focus in [A] at given index [I].
 */
public fun <T, S, I, A> Lens<T, S>.at(at: At<S, I, A>, i: I): Lens<T, A> = this.compose(at.at(i))

/**
 * DSL to compose [At] with an [Optional] for a structure [S] to focus in on [A] at given index [I].
 *
 * @receiver [Optional] or [Prism] with a focus in [S]
 * @param at [At] instance to provide a [Lens] to zoom into [S] at [I]
 * @param i index [I] to zoom into [S] and find focus [A]
 * @return [Optional] with a focus in [A] at given index [I].
 */
public fun <T, S, I, A> Optional<T, S>.at(at: At<S, I, A>, i: I): Optional<T, A> = this.compose(at.at(i))

/**
 * DSL to compose [At] with a [Traversal] for a structure [S] to focus in on [A] at given index [I].
 *
 * @receiver [Traversal] with a focus in [S]
 * @param at [At] instance to provide a [Lens] to zoom into [S] at [I]
 * @param i index [I] to zoom into [S] and find focus [A]
 * @return [Traversal] with a focus in [A] at given index [I].
 */
public fun <T, S, I, A> Traversal<T, S>.at(at: At<S, I, A>, i: I): Traversal<T, A> = this.compose(at.at(i))

public fun <T, K, V> Lens<T, Map<K, V>>.at(key: K): Lens<T, Option<V>> =
  this.compose(At.map<K, V>().at(key))

public fun <T, K, V> Optional<T, Map<K, V>>.at(key: K): Optional<T, Option<V>> =
  this.compose(At.map<K, V>().at(key))

public fun <T, K, V> Traversal<T, Map<K, V>>.at(key: K): Traversal<T, Option<V>> =
  this.compose(At.map<K, V>().at(key))

@JvmName("atSet")
public fun <T, A> Lens<T, Set<A>>.at(value: A): Lens<T, Boolean> =
  this.compose(At.set<A>().at(value))

@JvmName("atSet")
public fun <T, A> Optional<T, Set<A>>.at(value: A): Optional<T, Boolean> =
  this.compose(At.set<A>().at(value))

@JvmName("atSet")
public fun <T, A> Traversal<T, Set<A>>.at(value: A): Traversal<T, Boolean> =
  this.compose(At.set<A>().at(value))

