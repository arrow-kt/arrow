@file:Suppress("API_NOT_AVAILABLE")
@file:OptIn(ExperimentalContracts::class)
package arrow.core

import arrow.core.MonotoneCollectionBuilder.NonEmpty
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * A builder for collections that can only grow by adding elements.
 * Removing elements is not supported to preserve monotonicity.
 */
@SubclassOptInRequired(PotentiallyUnsafeNonEmptyOperation::class)
public interface MonotoneCollectionBuilder<in E> {
  // underscored so that addAll(Nec<E>) extension overload takes precedence
  // Turning this into an abstract class doesn't work well because of KT-83602
  public fun _addAll(elements: Collection<E>)

  public fun _add(element: E)

  /**
   * Marker interface for builders that guarantee at least one element has been added.
   * @see MonotoneCollectionBuilder
   * @see buildNonEmptyList
   */
  public interface NonEmpty
}

public fun <E> MonotoneCollectionBuilder<E>.add(element: E) {
  contract { returns() implies (this@add is NonEmpty) }
  return _add(element)
}

public fun <E> MonotoneCollectionBuilder<E>.addAll(elements: Iterable<E>): Unit = when (elements) {
  is Collection -> addAll(elements)
  else -> for (item in elements) add(item)
}

public fun <E> MonotoneCollectionBuilder<E>.addAll(elements: NonEmptyCollection<E>) {
  contract { returns() implies (this@addAll is NonEmpty) }
  _addAll(elements)
}

public fun <E> MonotoneCollectionBuilder<E>.addAll(elements: Collection<E>): Unit = _addAll(elements)

@SubclassOptInRequired(PotentiallyUnsafeNonEmptyOperation::class)
private abstract class MonotoneMutableCollectionImpl<E>(private val underlying: MutableCollection<E>) : MonotoneCollectionBuilder<E>, NonEmpty {
  final override fun _addAll(elements: Collection<E>) { underlying.addAll(elements) }
  final override fun _add(element: E) { underlying.add(element) }

  final override fun equals(other: Any?) = underlying == other
  final override fun hashCode() = underlying.hashCode()
  final override fun toString() = underlying.toString()
}

/**
 * A mutable list that can only grow by adding elements.
 * Removing elements is not supported to preserve monotonicity.
 */
@SubclassOptInRequired(PotentiallyUnsafeNonEmptyOperation::class)
public interface MonotoneMutableList<E>: MonotoneCollectionBuilder<E>, List<E> {
  public companion object {
    public operator fun <E> invoke(): MonotoneMutableList<E> = Impl()

    public operator fun <E> invoke(initialCapacity: Int): MonotoneMutableList<E> = Impl(initialCapacity)
  }

  @OptIn(PotentiallyUnsafeNonEmptyOperation::class)
  private class Impl<E> private constructor(underlying: MutableList<E>) : MonotoneMutableCollectionImpl<E>(underlying), MonotoneMutableList<E>, List<E> by underlying {
    constructor() : this(ArrayList())
    constructor(initialCapacity: Int) : this(ArrayList(initialCapacity))
  }
}

@OptIn(PotentiallyUnsafeNonEmptyOperation::class)
public fun <E, L> L.asNonEmptyList(): NonEmptyList<E> where L : List<E>, L : NonEmpty = NonEmptyList(this)

public inline fun <E, L> buildNonEmptyList(
  builderAction: MonotoneMutableList<E>.() -> L
): NonEmptyList<E> where L : List<E>, L : NonEmpty {
  contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
  return builderAction(MonotoneMutableList()).asNonEmptyList()
}

public inline fun <E, L> buildNonEmptyList(
  capacity: Int,
  builderAction: MonotoneMutableList<E>.() -> L
): NonEmptyList<E> where L : List<E>, L : NonEmpty {
  contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
  return builderAction(MonotoneMutableList(capacity)).asNonEmptyList()
}

/**
 * A mutable list that can only grow by adding elements.
 * Removing elements is not supported to preserve monotonicity.
 */
@SubclassOptInRequired(PotentiallyUnsafeNonEmptyOperation::class)
public interface MonotoneMutableSet<E>: MonotoneCollectionBuilder<E>, Set<E> {
  public companion object {
    public operator fun <E> invoke(): MonotoneMutableSet<E> = Impl()
    public operator fun <E> invoke(initialCapacity: Int): MonotoneMutableSet<E> = Impl(initialCapacity)
  }

  @OptIn(PotentiallyUnsafeNonEmptyOperation::class)
  private class Impl<E> private constructor(underlying: MutableSet<E>) : MonotoneMutableCollectionImpl<E>(underlying), MonotoneMutableSet<E>, NonEmpty, Set<E> by underlying {
    constructor() : this(LinkedHashSet())
    constructor(initialCapacity: Int) : this(LinkedHashSet(initialCapacity))
  }
}

@OptIn(PotentiallyUnsafeNonEmptyOperation::class)
public fun <E, S> S.asNonEmptySet(): NonEmptySet<E> where S : Set<E>, S : NonEmpty = NonEmptySet(this)

public inline fun <E, S> buildNonEmptySet(
  builderAction: MonotoneMutableSet<E>.() -> S
): NonEmptySet<E> where S : Set<E>, S : NonEmpty {
  contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
  return builderAction(MonotoneMutableSet()).asNonEmptySet()
}

public inline fun <E, S> buildNonEmptySet(
  capacity: Int,
  builderAction: MonotoneMutableSet<E>.() -> S
): NonEmptySet<E> where S : Set<E>, S : NonEmpty {
  contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
  return builderAction(MonotoneMutableSet(capacity)).asNonEmptySet()
}
