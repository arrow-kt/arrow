package arrow.core

public class NonEmptySet<out T> private constructor(
  private val elements: Set<T>
) : Set<T> by elements {

  public constructor(first: T, rest: Set<T>): this(setOf(first) + rest)

  public operator fun plus(set: Set<@UnsafeVariance T>): NonEmptySet<T> = NonEmptySet(elements + set)

  public operator fun plus(element: @UnsafeVariance T): NonEmptySet<T> = NonEmptySet(elements + element)

  override fun toString(): String = "NonEmptySet(${this.joinToString()})"

  override fun equals(other: Any?): Boolean {
    return elements == other
  }

  override fun hashCode(): Int {
    return elements.hashCode()
  }
}

public fun <T> nonEmptySetOf(fist: T, vararg rest: T): NonEmptySet<T> =
  NonEmptySet(fist, rest.toSet())

public fun <T> Collection<T>.toNonEmptySetOrNull(): NonEmptySet<T>? =
  firstOrNull()?.let { NonEmptySet(it, minus(it).toSet()) }

public fun <T> Collection<T>.toNonEmptySetOrNone(): Option<NonEmptySet<T>> =
  toNonEmptySetOrNull().toOption()
