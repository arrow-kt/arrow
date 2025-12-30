@file:OptIn(ExperimentalTypeInference::class, ExperimentalStdlibApi::class, ExperimentalContracts::class)
@file:Suppress("API_NOT_AVAILABLE", "RESERVED_MEMBER_INSIDE_VALUE_CLASS")

package arrow.core

import arrow.core.raise.RaiseAccumulate
import arrow.core.raise.either
import arrow.core.raise.mapOrAccumulate
import arrow.core.raise.withError
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmExposeBoxed
import kotlin.jvm.JvmInline
import kotlin.jvm.JvmStatic

@OptIn(PotentiallyUnsafeNonEmptyOperation::class)
@JvmInline
public value class NonEmptySet<out E> @PotentiallyUnsafeNonEmptyOperation internal constructor(
  @PublishedApi internal val elements: Set<E>
) : Set<E> by elements, NonEmptyCollection<E> {
  public constructor(first: E, rest: Iterable<E>) : this(buildNonEmptySet<E, _>(rest.collectionSizeOrDefault(10) + 1) {
    add(first)
    addAll(rest)
    this
  }.elements)

  public override operator fun plus(elements: Iterable<@UnsafeVariance E>): NonEmptySet<E> = buildNonEmptySet(size + elements.collectionSizeOrDefault(10)) {
    addAll(this@NonEmptySet)
    addAll(elements)
    this
  }

  public override operator fun plus(element: @UnsafeVariance E): NonEmptySet<E> = buildNonEmptySet(size + 1) {
    addAll(elements)
    add(element)
    this
  }

  override fun isEmpty(): Boolean = false

  @JvmExposeBoxed @Suppress("USELESS_JVM_EXPOSE_BOXED")
  public fun toSet(): Set<E> = elements

  override fun toString(): String = elements.toString()

  override fun equals(other: Any?): Boolean =
    elements == other

  override fun hashCode(): Int =
    elements.hashCode()

  public override fun distinct(): NonEmptyList<E> = toNonEmptyList()

  // Copied from NonEmptyCollection due to compilation bug with value classes and interface default methods
  override fun <K> distinctBy(selector: (E) -> K): NonEmptyList<E> = buildNonEmptyList(size) {
    add(head) // head is always distinct
    val seen = hashSetOf<K>()
    var isFirst = true
    for (e in elements) {
      if (seen.add(selector(e)) && !isFirst) add(e)
      isFirst = false
    }
    this
  }
  override fun <T> flatMap(transform: (E) -> NonEmptyCollection<T>): NonEmptyList<T> = buildNonEmptyList(size) {
    val iterator = elements.iterator()
    do addAll(transform(iterator.next())) while (iterator.hasNext())
    this
  }
  override fun <T> map(transform: (E) -> T): NonEmptyList<T> = buildNonEmptyList(size) {
    val iterator = elements.iterator()
    do add(transform(iterator.next())) while (iterator.hasNext())
    this
  }
  override fun <T> mapIndexed(transform: (index:Int, E) -> T): NonEmptyList<T> = buildNonEmptyList(size) {
    var i = 0
    val iterator = elements.iterator()
    do add(transform(i++, iterator.next())) while (iterator.hasNext())
    this
  }

  override fun <T> zip(other: NonEmptyCollection<T>): NonEmptyList<Pair<E, T>> = buildNonEmptyList(minOf(size, other.size)) {
    val first = this@NonEmptySet.iterator()
    val second = other.iterator()
    do add(first.next() to second.next()) while (first.hasNext() && second.hasNext())
    this
  }

  public companion object {
    @JvmStatic @JvmExposeBoxed
    public fun <E> of(head: E, vararg t: E): NonEmptySet<E> =
      nonEmptySetOf(head, *t)

    @JvmStatic @JvmExposeBoxed
    public fun <E> of(values: Iterable<E>): NonEmptySet<E> =
      values.toNonEmptySetOrThrow()
  }
}

public inline fun <Error, E, T> NonEmptySet<E>.mapOrAccumulate(
  combine: (Error, Error) -> Error,
  @BuilderInference transform: RaiseAccumulate<Error>.(E) -> T
): Either<Error, NonEmptySet<T>> = either {
  withError({ it.reduce(combine) }) { mapOrAccumulate(this@mapOrAccumulate, transform) }
}

public inline fun <Error, E, T> NonEmptySet<E>.mapOrAccumulate(
  @BuilderInference transform: RaiseAccumulate<Error>.(E) -> T
): Either<NonEmptyList<Error>, NonEmptySet<T>> = either {
  mapOrAccumulate(this@mapOrAccumulate, transform)
}

public fun <E> nonEmptySetOf(first: E, vararg rest: E): NonEmptySet<E> =
  NonEmptySet(first, rest.asIterable())

/**
 * Returns a [NonEmptySet] that contains a **copy** of the elements in [this].
 */
public fun <T> Iterable<T>.toNonEmptySetOrNull(): NonEmptySet<T>? = MonotoneMutableSet<T>(collectionSizeOrDefault(10)).run {
  addAll(this@toNonEmptySetOrNull)
  if (isNonEmpty()) asNonEmptySet() else null
}

/**
 * Returns a [NonEmptySet] that contains a **copy** of the elements in [this].
 */
public fun <T> Iterable<T>.toNonEmptySetOrNone(): Option<NonEmptySet<T>> =
  toNonEmptySetOrNull().toOption()

/**
 * Returns a [NonEmptySet] that contains a **copy** of the elements in [this].
 */
public fun <T> Iterable<T>.toNonEmptySetOrThrow(): NonEmptySet<T> = toNonEmptySetOrNull() ?: throw IllegalArgumentException("Cannot create NonEmptySet from empty Iterable")

@Deprecated("Same as Iterable extension", level = DeprecationLevel.HIDDEN)
public fun <E> Set<E>.toNonEmptySetOrNull(): NonEmptySet<E>? =
  (this as Iterable<E>).toNonEmptySetOrNull()

@Deprecated("Same as Iterable extension", level = DeprecationLevel.HIDDEN)
public fun <E> Set<E>.toNonEmptySetOrNone(): Option<NonEmptySet<E>> =
  toNonEmptySetOrNull().toOption()

/**
 * Returns a [NonEmptySet] that wraps the given [this], avoiding an additional copy.
 *
 * Any modification made to [this] will also be visible through the returned [NonEmptySet].
 * You are responsible for keeping the non-emptiness invariant at all times.
 */
@PotentiallyUnsafeNonEmptyOperation
public fun <T> Set<T>.wrapAsNonEmptySetOrThrow(): NonEmptySet<T> = wrapAsNonEmptySetOrNull() ?: throw IllegalArgumentException("Cannot create NonEmptySet from empty Set")

/**
 * Returns a [NonEmptySet] that wraps the given [this], avoiding an additional copy.
 *
 * Any modification made to [this] will also be visible through the returned [NonEmptySet].
 * You are responsible for keeping the non-emptiness invariant at all times.
 */
@PotentiallyUnsafeNonEmptyOperation
public fun <T> Set<T>.wrapAsNonEmptySetOrNull(): NonEmptySet<T>? = when {
  isEmpty() -> null
  else -> NonEmptySet(this)
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
