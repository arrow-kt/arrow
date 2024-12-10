@file:OptIn(ExperimentalTypeInference::class)

package arrow.core

import arrow.core.raise.RaiseAccumulate
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmInline

@JvmInline
public value class NonEmptySet<out E> private constructor(
  @PublishedApi internal val elements: Set<E>
) : Set<E> by elements, NonEmptyCollection<E> {

  public constructor(first: E, rest: Iterable<E>) : this(setOf(first) + rest)

  public override operator fun plus(elements: Iterable<@UnsafeVariance E>): NonEmptySet<E> =
    NonEmptySet(this.elements + elements)

  public override operator fun plus(element: @UnsafeVariance E): NonEmptySet<E> =
    NonEmptySet(this.elements + element)

  override fun isEmpty(): Boolean = false

  override val head: E get() = elements.first()

  override fun lastOrNull(): E = elements.last()

  override fun toString(): String = "NonEmptySet(${this.joinToString()})"

  @Suppress("RESERVED_MEMBER_INSIDE_VALUE_CLASS")
  override fun equals(other: Any?): Boolean =
    elements == other

  @Suppress("RESERVED_MEMBER_INSIDE_VALUE_CLASS")
  override fun hashCode(): Int =
    elements.hashCode()

  public override fun distinct(): NonEmptyList<E> =
    NonEmptyList(elements.distinct())

  public override fun <K> distinctBy(selector: (E) -> K): NonEmptyList<E> =
    NonEmptyList(elements.distinctBy(selector))

  public override fun <T> map(transform: (E) -> T): NonEmptyList<T> =
    NonEmptyList(elements.map(transform))

  public override fun <T> flatMap(transform: (E) -> NonEmptyCollection<T>): NonEmptyList<T> =
    NonEmptyList(elements.flatMap(transform))

  public override fun <T> mapIndexed(transform: (index: Int, E) -> T): NonEmptyList<T> =
    NonEmptyList(elements.mapIndexed(transform))

  override fun <T> zip(other: NonEmptyCollection<T>): NonEmptyList<Pair<E, T>> =
    NonEmptyList(elements.zip(other))
}

public inline fun <Error, E, T> NonEmptySet<E>.mapOrAccumulate(
  combine: (Error, Error) -> Error,
  @BuilderInference transform: RaiseAccumulate<Error>.(E) -> T
): Either<Error, NonEmptySet<T>> =
  elements.mapOrAccumulate(combine, transform).map { requireNotNull(it.toNonEmptySetOrNull()) }

public inline fun <Error, E, T> NonEmptySet<E>.mapOrAccumulate(
  @BuilderInference transform: RaiseAccumulate<Error>.(E) -> T
): Either<NonEmptyList<Error>, NonEmptySet<T>> =
  elements.mapOrAccumulate(transform).map { requireNotNull(it.toNonEmptySetOrNull()) }

public fun <E> nonEmptySetOf(first: E, vararg rest: E): NonEmptySet<E> =
  NonEmptySet(first, rest.asIterable())

public fun <T> Iterable<T>.toNonEmptySetOrNull(): NonEmptySet<T>? {
  val iter = iterator()
  if (!iter.hasNext()) return null
  return NonEmptySet(iter.next(), Iterable { iter })
}

public fun <T> Iterable<T>.toNonEmptySetOrNone(): Option<NonEmptySet<T>> =
  toNonEmptySetOrNull().toOption()

@Deprecated("Same as Iterable extension", level = DeprecationLevel.HIDDEN)
public fun <E> Set<E>.toNonEmptySetOrNull(): NonEmptySet<E>? =
  (this as Iterable<E>).toNonEmptySetOrNull()

@Deprecated("Same as Iterable extension", level = DeprecationLevel.HIDDEN)
public fun <E> Set<E>.toNonEmptySetOrNone(): Option<NonEmptySet<E>> =
  toNonEmptySetOrNull().toOption()
