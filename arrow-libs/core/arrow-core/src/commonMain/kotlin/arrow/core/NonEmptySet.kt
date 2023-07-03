package arrow.core

import kotlin.jvm.JvmInline

@JvmInline
public value class NonEmptySet<out A> private constructor(
  private val elements: Set<A>
) : Set<A> by elements {

  public constructor(first: A, rest: Set<A>) : this(setOf(first) + rest)

  public operator fun plus(set: Set<@UnsafeVariance A>): NonEmptySet<A> =
    NonEmptySet(elements + set)

  public operator fun plus(element: @UnsafeVariance A): NonEmptySet<A> =
    NonEmptySet(elements + element)

  public fun <R> map(transform: (@UnsafeVariance A) -> R): NonEmptySet<R> =
    NonEmptySet(elements.mapTo(mutableSetOf(), transform))

  override fun isEmpty(): Boolean = false

  override fun toString(): String = "NonEmptySet(${this.joinToString()})"

  @Suppress("RESERVED_MEMBER_INSIDE_VALUE_CLASS")
  override fun equals(other: Any?): Boolean = when (other) {
    is NonEmptySet<*> -> elements == other.elements
    else -> elements == other
  }

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
