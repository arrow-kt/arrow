@file:Suppress("API_NOT_AVAILABLE")
@file:OptIn(ExperimentalContracts::class)
package arrow.core

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


/**
 * A mutable collection that can only grow by adding elements.
 * Removing elements is not supported to preserve monotonicity.
 */
@SubclassOptInRequired(PotentiallyUnsafeNonEmptyOperation::class)
public interface MonotoneMutableCollection<E>: Collection<E> {
  // underscored so that addAll(Nec<E>) extension overload takes precedence
  @IgnorableReturnValue
  public fun _addAll(elements: Collection<E>): Boolean

  @IgnorableReturnValue
  public fun _add(element: E): Boolean
}

@IgnorableReturnValue
public fun <E> MonotoneMutableCollection<E>.add(element: E): Boolean {
  contract { returns() implies (this@add is NonEmptyCollection<E>) }
  return _add(element)
}

@IgnorableReturnValue
public fun <E> MonotoneMutableCollection<E>.addAll(elements: Iterable<E>): Boolean = when (elements) {
  is Collection -> addAll(elements)
  else -> {
    var result = false
    for (item in elements) if (add(item)) result = true
    result
  }
}

@OptIn(ExperimentalContracts::class)
@IgnorableReturnValue
public fun <E> MonotoneMutableCollection<E>.addAll(elements: NonEmptyCollection<E>): Boolean {
  contract { returns() implies (this@addAll is NonEmptyCollection<E>) }
  return _addAll(elements)
}

@OptIn(ExperimentalContracts::class)
@IgnorableReturnValue
public fun <E> MonotoneMutableCollection<E>.addAll(elements: Collection<E>): Boolean = _addAll(elements)

public fun <E> MonotoneMutableCollection<E>.isNonEmpty(): Boolean {
  contract { returns(true) implies (this@isNonEmpty is NonEmptyCollection<E>) }
  return isNotEmpty()
}

/**
 * A mutable list that can only grow by adding elements.
 * Removing elements is not supported to preserve monotonicity.
 */
@SubclassOptInRequired(PotentiallyUnsafeNonEmptyOperation::class)
public interface MonotoneMutableList<E>: MonotoneMutableCollection<E>, List<E> {
  @IgnorableReturnValue
  public fun _addAll(index: Int, elements: Collection<E>): Boolean

  @IgnorableReturnValue
  public operator fun set(index: Int, element: E): E

  public fun _add(index: Int, element: E)

  override fun listIterator(): Iterator<E>

  override fun listIterator(index: Int): Iterator<E>

  override fun subList(fromIndex: Int, toIndex: Int): MonotoneMutableList<E>

  public companion object {
    public operator fun <E> invoke(): MonotoneMutableList<E> = Impl()

    public operator fun <E> invoke(initialCapacity: Int): MonotoneMutableList<E> = Impl(initialCapacity)
  }

  public interface Iterator<T> : ListIterator<T> {
    public fun set(element: T)

    public fun add(element: T)
  }

  private class IteratorImpl<T>(private val underlying: MutableListIterator<T>) : Iterator<T>, ListIterator<T> by underlying {
    override fun set(element: T) = underlying.set(element)

    override fun add(element: T) = underlying.add(element)
  }

  @OptIn(PotentiallyUnsafeNonEmptyOperation::class)
  private class Impl<E> private constructor(private val underlying: MutableList<E>) : MonotoneMutableList<E>, NonEmptyCollection<E>, List<E> by underlying {
    constructor() : this(ArrayList())
    constructor(initialCapacity: Int) : this(ArrayList(initialCapacity))

    override fun isEmpty(): Boolean = underlying.isEmpty()

    override fun _addAll(index: Int, elements: Collection<E>) = underlying.addAll(index, elements)

    override fun set(index: Int, element: E) = underlying.set(index, element)

    override fun _add(index: Int, element: E) = underlying.add(index, element)

    override fun _addAll(elements: Collection<E>) = underlying.addAll(elements)

    override fun _add(element: E) = underlying.add(element)

    override fun listIterator() = IteratorImpl(underlying.listIterator())
    override fun listIterator(index: Int) = IteratorImpl(underlying.listIterator(index))
    override fun subList(fromIndex: Int, toIndex: Int) = Impl(underlying.subList(fromIndex, toIndex))
    override fun plus(element: E) = asNonEmptyList() + element
    override fun plus(elements: Iterable<E>) = asNonEmptyList() + elements
    override fun equals(other: Any?) = underlying == other
    override fun hashCode() = underlying.hashCode()
    override fun toString() = underlying.toString()
  }
}

@IgnorableReturnValue
public fun <E> MonotoneMutableList<E>.addAll(index: Int, elements: Collection<E>): Boolean = _addAll(index, elements)

@IgnorableReturnValue
public fun <E> MonotoneMutableList<E>.addAll(index: Int, elements: NonEmptyCollection<E>): Boolean {
  contract { returns() implies (this@addAll is NonEmptyCollection<E>) }
  return _addAll(index, elements)
}

public fun <E> MonotoneMutableList<E>.add(index: Int, element: E) {
  contract { returns() implies (this@add is NonEmptyCollection<E>) }
  _add(index, element)
}

@OptIn(PotentiallyUnsafeNonEmptyOperation::class)
public fun <E, L> L.asNonEmptyList(): NonEmptyList<E> where L : List<E>, L : NonEmptyCollection<E> = NonEmptyList(this)

public inline fun <E, L> buildNonEmptyList(
  builderAction: MonotoneMutableList<E>.() -> L
): NonEmptyList<E> where L : List<E>, L : NonEmptyCollection<E> {
  contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
  return builderAction(MonotoneMutableList()).asNonEmptyList()
}

public inline fun <E, L> buildNonEmptyList(
  capacity: Int,
  builderAction: MonotoneMutableList<E>.() -> L
): NonEmptyList<E> where L : List<E>, L : NonEmptyCollection<E> {
  contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
  return builderAction(MonotoneMutableList(capacity)).asNonEmptyList()
}

/**
 * A mutable list that can only grow by adding elements.
 * Removing elements is not supported to preserve monotonicity.
 */
@SubclassOptInRequired(PotentiallyUnsafeNonEmptyOperation::class)
public interface MonotoneMutableSet<E>: MonotoneMutableCollection<E>, Set<E> {
  public companion object {
    public operator fun <E> invoke(): MonotoneMutableSet<E> = Impl()
    public operator fun <E> invoke(initialCapacity: Int): MonotoneMutableSet<E> = Impl(initialCapacity)
  }

  @OptIn(PotentiallyUnsafeNonEmptyOperation::class)
  private class Impl<E> private constructor(private val underlying: MutableSet<E>) : MonotoneMutableSet<E>, NonEmptyCollection<E>, Set<E> by underlying {
    constructor() : this(LinkedHashSet())
    constructor(initialCapacity: Int) : this(LinkedHashSet(initialCapacity))

    override fun isEmpty(): Boolean = underlying.isEmpty()

    override fun _addAll(elements: Collection<E>) = underlying.addAll(elements)

    override fun _add(element: E) = underlying.add(element)

    override fun plus(element: E) = buildNonEmptySet<E, _>(size + 1) {
      addAll(this@Impl)
      add(element)
      this
    }

    override fun plus(elements: Iterable<E>) = buildNonEmptySet<E, _>(size) {
      addAll(this@Impl)
      addAll(elements)
      this
    }

    override fun equals(other: Any?) = underlying == other
    override fun hashCode() = underlying.hashCode()
    override fun toString() = underlying.toString()
  }
}

@OptIn(PotentiallyUnsafeNonEmptyOperation::class)
public fun <E, S> S.asNonEmptySet(): NonEmptySet<E> where S : Set<E>, S : NonEmptyCollection<E> = NonEmptySet(this)

public inline fun <E, S> buildNonEmptySet(
  builderAction: MonotoneMutableSet<E>.() -> S
): NonEmptySet<E> where S : Set<E>, S : NonEmptyCollection<E> {
  contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
  return builderAction(MonotoneMutableSet()).asNonEmptySet()
}

public inline fun <E, S> buildNonEmptySet(
  capacity: Int = 0,
  builderAction: MonotoneMutableSet<E>.() -> S
): NonEmptySet<E> where S : Set<E>, S : NonEmptyCollection<E> {
  contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
  return builderAction(MonotoneMutableSet(capacity)).asNonEmptySet()
}
