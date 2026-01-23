@file:OptIn(ExperimentalTypeInference::class, ExperimentalStdlibApi::class)
@file:Suppress("API_NOT_AVAILABLE", "RESERVED_MEMBER_INSIDE_VALUE_CLASS")

package arrow.core

import arrow.core.raise.RaiseAccumulate
import arrow.core.raise.either
import arrow.core.raise.mapOrAccumulate
import arrow.core.raise.withError
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

  // BEGIN: overrides due to KT-80101 NoSuchMethodError when inheriting function implementations in interfaces
  public override fun <K> distinctBy(selector: (E) -> K): NonEmptyList<E> = super.distinctBy(selector)

  public override fun <T> map(transform: (E) -> T): NonEmptyList<T> = super.map(transform)

  public override fun <T> flatMap(transform: (E) -> NonEmptyCollection<T>): NonEmptyList<T> = super.flatMap(transform)

  public override fun <T> mapIndexed(transform: (index: Int, E) -> T): NonEmptyList<T> = super.mapIndexed(transform)
  // END

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
@OptIn(PotentiallyUnsafeNonEmptyOperation::class)
public fun <T> Iterable<T>.toNonEmptySetOrNull(): NonEmptySet<T>? = toSet().wrapAsNonEmptySetOrNull()

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
