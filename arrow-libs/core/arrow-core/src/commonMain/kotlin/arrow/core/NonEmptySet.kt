package arrow.core

import kotlin.jvm.JvmInline

@JvmInline
public value class NonEmptySet<out A> private constructor(
  private val elements: Set<A>
) : Set<A> by elements, NonEmptyCollection<A> {

  public constructor(first: A, rest: Set<A>) : this(setOf(first) + rest)

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
}

public fun <A> nonEmptySetOf(first: A, vararg rest: A): NonEmptySet<A> =
  NonEmptySet(first, rest.toSet())

public fun <A> Iterable<A>.toNonEmptySetOrNull(): NonEmptySet<A>? =
  firstOrNull()?.let { NonEmptySet(it, minus(it).toSet()) }

public fun <A> Iterable<A>.toNonEmptySetOrNone(): Option<NonEmptySet<A>> =
  toNonEmptySetOrNull().toOption()

public fun <A> Set<A>.toNonEmptySetOrNull(): NonEmptySet<A>? =
  firstOrNull()?.let { NonEmptySet(it, minus(it)) }

public fun <A> Set<A>.toNonEmptySetOrNone(): Option<NonEmptySet<A>> =
  toNonEmptySetOrNull().toOption()
