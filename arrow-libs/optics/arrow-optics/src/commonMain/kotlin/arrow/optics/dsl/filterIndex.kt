package arrow.optics.dsl

import arrow.core.NonEmptyList
import arrow.core.Predicate
import arrow.optics.Optional
import arrow.optics.Traversal
import arrow.optics.typeclasses.FilterIndex
import kotlin.jvm.JvmName

/**
 * DSL to compose [FilterIndex] with an [Traversal] for a structure [S] to focus in on [A] at given index [I]
 *
 * @receiver [Optional] with a focus in [S]
 * @param filter [FilterIndex] instance to provide a [Optional] to focus into [S] at [I]
 * @param predicate index [I] to focus into [S] and find focus [A]
 * @return [Optional] with a focus in [A] at given index [I]
 */
public fun <T, S, I, A> Traversal<T, S>.filterIndex(filter: FilterIndex<S, I, A>, predicate: Predicate<I>): Traversal<T, A> =
  this.compose(filter.filter(predicate))

public fun <T, A> Traversal<T, List<A>>.filterIndex(predicate: Predicate<Int>): Traversal<T, A> =
  this.compose(FilterIndex.list<A>().filter(predicate))

@JvmName("filterNonEmptyList")
public fun <T, A> Traversal<T, NonEmptyList<A>>.filterIndex(predicate: Predicate<Int>): Traversal<T, A> =
  this.compose(FilterIndex.nonEmptyList<A>().filter(predicate))

@JvmName("filterSequence")
public fun <T, A> Traversal<T, Sequence<A>>.filterIndex(predicate: Predicate<Int>): Traversal<T, A> =
  this.compose(FilterIndex.sequence<A>().filter(predicate))

@JvmName("filterValues")
public fun <T, K, A> Traversal<T, Map<K, A>>.filterIndex(predicate: Predicate<K>): Traversal<T, A> =
  this.compose(FilterIndex.map<K, A>().filter(predicate))

@JvmName("filterChars")
public fun <T> Traversal<T, String>.filterIndex(predicate: Predicate<Int>): Traversal<T, Char> =
  this.compose(FilterIndex.string().filter(predicate))
