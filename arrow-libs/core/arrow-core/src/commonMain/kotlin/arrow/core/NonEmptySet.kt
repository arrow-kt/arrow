package arrow.core

public class NonEmptySet<out T>(
  private val first: T,
  rest: Set<T>,
) : AbstractSet<T>() {

  public val elements: Set<T>

  init {
      elements = setOf(first) + rest
  }

  public operator fun plus(other: NonEmptySet<@UnsafeVariance T>): NonEmptySet<T> =
    NonEmptySet(this.first, elements + other.elements)

  public operator fun plus(s: Set<@UnsafeVariance T>): NonEmptySet<T> =
    NonEmptySet(first, this.elements + s)

  public operator fun plus(s: @UnsafeVariance T): NonEmptySet<T> =
    NonEmptySet(first, this.elements.plus(s))

  override val size: Int
    get() = elements.size

  override fun iterator(): Iterator<T> = elements.iterator()

  override fun toString(): String = "NonEmptySet(${elements.joinToString()})"

  override fun equals(other: Any?): Boolean = when {
    other === this -> true
    other !is Set<*> -> false
    other is NonEmptySet<*> -> elements == other.elements
    else -> elements == other
  }

  override fun hashCode(): Int = elements.hashCode()
}

public fun <T> nonEmptySetOf(fist: T, vararg rest: T): NonEmptySet<Any?> =
  NonEmptySet(fist, rest.toSet())

public fun <T> Collection<T>.toNonEmptySetOrNull(): NonEmptySet<T>? =
  firstOrNull()?.let { NonEmptySet(it, this.toSet()) }

public fun <T> Collection<T>.toNonEmptySetOrNone(): Option<NonEmptySet<T>> =
  toNonEmptySetOrNull().toOption()
