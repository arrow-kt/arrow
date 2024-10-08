@file:OptIn(ExperimentalTypeInference::class)

package arrow.core

import arrow.core.raise.RaiseAccumulate
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmInline

@JvmInline
public value class NonEmptySet<out A> private constructor(
  @PublishedApi internal val elements: Set<A>
) : Set<A> by elements, NonEmptyCollection<A> {

  public constructor(first: A, rest: Iterable<A>) : this(setOf(first) + rest)

  public override operator fun plus(elements: Iterable<@UnsafeVariance A>): NonEmptySet<A> =
    NonEmptySet(this.elements + elements)

  public override operator fun plus(element: @UnsafeVariance A): NonEmptySet<A> =
    NonEmptySet(this.elements + element)

  override fun isEmpty(): Boolean = false

  override val head: A get() = elements.first()

  override fun lastOrNull(): A = elements.last()

  override fun toString(): String = "NonEmptySet(${this.joinToString()})"

  @Suppress("RESERVED_MEMBER_INSIDE_VALUE_CLASS")
  override fun equals(other: Any?): Boolean =
    elements == other

  @Suppress("RESERVED_MEMBER_INSIDE_VALUE_CLASS")
  override fun hashCode(): Int =
    elements.hashCode()

  public override fun distinct(): NonEmptyList<A> =
    NonEmptyList(elements.distinct())

  public override fun <K> distinctBy(selector: (A) -> K): NonEmptyList<A> =
    NonEmptyList(elements.distinctBy(selector))

  public override fun <B> map(transform: (A) -> B): NonEmptyList<B> =
    NonEmptyList(elements.map(transform))

  public override fun <B> flatMap(transform: (A) -> NonEmptyCollection<B>): NonEmptyList<B> =
    NonEmptyList(elements.flatMap(transform))

  public override fun <B> mapIndexed(transform: (index: Int, A) -> B): NonEmptyList<B> =
    NonEmptyList(elements.mapIndexed(transform))

  override fun <B> zip(other: NonEmptyCollection<B>): NonEmptyList<Pair<A, B>> =
    NonEmptyList(elements.zip(other))
}

public inline fun <E, A, B> NonEmptySet<A>.mapOrAccumulate(
  combine: (E, E) -> E,
  @BuilderInference transform: RaiseAccumulate<E>.(A) -> B
): Either<E, NonEmptySet<B>> =
  elements.mapOrAccumulate(combine, transform).map { requireNotNull(it.toNonEmptySetOrNull()) }

public inline fun <E, A, B> NonEmptySet<A>.mapOrAccumulate(
  @BuilderInference transform: RaiseAccumulate<E>.(A) -> B
): Either<NonEmptyList<E>, NonEmptySet<B>> =
  elements.mapOrAccumulate(transform).map { requireNotNull(it.toNonEmptySetOrNull()) }

public fun <A> nonEmptySetOf(first: A, vararg rest: A): NonEmptySet<A> =
  NonEmptySet(first, rest.asIterable())

public fun <A> Iterable<A>.toNonEmptySetOrNull(): NonEmptySet<A>? {
  val iter = iterator()
  if (!iter.hasNext()) return null
  return NonEmptySet(iter.next(), Iterable { iter })
}

public fun <A> Iterable<A>.toNonEmptySetOrNone(): Option<NonEmptySet<A>> =
  toNonEmptySetOrNull().toOption()

@Deprecated("Same as Iterable extension", level = DeprecationLevel.HIDDEN)
public fun <A> Set<A>.toNonEmptySetOrNull(): NonEmptySet<A>? =
  (this as Iterable<A>).toNonEmptySetOrNull()

@Deprecated("Same as Iterable extension", level = DeprecationLevel.HIDDEN)
public fun <A> Set<A>.toNonEmptySetOrNone(): Option<NonEmptySet<A>> =
  toNonEmptySetOrNull().toOption()
