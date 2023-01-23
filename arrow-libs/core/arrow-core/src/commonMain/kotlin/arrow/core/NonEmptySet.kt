package arrow.core

public class NonEmptySet<out T>(
  private val first: T,
  private val rest: Set<T>,
) : AbstractSet<T>() {

  public val elements: Set<T> by lazy { setOf(first) + rest }

  public operator fun plus(other: NonEmptySet<@UnsafeVariance T>): NonEmptySet<T> =
    NonEmptySet(this.first, rest + other.elements)

  public operator fun plus(s: Set<@UnsafeVariance T>): NonEmptySet<T> =
    NonEmptySet(first, rest + s)

  public operator fun plus(s: @UnsafeVariance T): NonEmptySet<T> =
    NonEmptySet(first, rest + s)

  override val size: Int
    get() = elements.size

  override fun iterator(): Iterator<T> = elements.iterator()

  override fun toString(): String = "NonEmptySet(${elements.joinToString()})"
}

public fun <T> nonEmptySetOf(fist: T, vararg rest: T): NonEmptySet<Any?> =
  NonEmptySet(fist, rest.toSet())

public fun <T> Collection<T>.toNonEmptySetOrNull(): NonEmptySet<T>? =
  firstOrNull()?.let { NonEmptySet(it, this.drop(1).toSet()) }

public fun <T> Collection<T>.toNonEmptySetOrNone(): Option<NonEmptySet<T>> =
  toNonEmptySetOrNull().toOption()
