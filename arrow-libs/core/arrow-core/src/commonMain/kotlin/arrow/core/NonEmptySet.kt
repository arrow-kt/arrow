package arrow.core

import kotlin.jvm.JvmInline

@JvmInline
public value class NonEmptySet<out T> private constructor(
  private val elements: Set<T>
) : Set<T> by elements {

  public constructor(first: T, rest: Set<T>) : this(setOf(first) + rest)

  public operator fun plus(set: Set<@UnsafeVariance T>): NonEmptySet<T> =
    NonEmptySet(elements + set)

  public operator fun plus(element: @UnsafeVariance T): NonEmptySet<T> =
    NonEmptySet(elements + element)

  public fun <R> map(transform: (@UnsafeVariance T) -> R): NonEmptySet<R> =
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

public fun <T> nonEmptySetOf(first: T, vararg rest: T): NonEmptySet<T> =
  NonEmptySet(first, rest.toSet())

public fun <T> Collection<T>.toNonEmptySetOrNull(): NonEmptySet<T>? =
  firstOrNull()?.let { NonEmptySet(it, minus(it).toSet()) }

public fun <T> Collection<T>.toNonEmptySetOrNone(): Option<NonEmptySet<T>> =
  toNonEmptySetOrNull().toOption()
