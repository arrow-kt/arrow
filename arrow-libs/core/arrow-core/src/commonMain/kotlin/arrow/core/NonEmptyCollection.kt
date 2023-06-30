package arrow.core

/**
 * Common interface for collections that always have
 * at least one element (available from [head]).
 */
public interface NonEmptyCollection<out E> : Collection<E> {
  override fun isEmpty(): Boolean = false
  public val head: E

  public operator fun plus(element: @UnsafeVariance E): NonEmptyCollection<E>
  public operator fun plus(elements: Iterable<@UnsafeVariance E>): NonEmptyCollection<E>

  public fun <B> map(transform: (E) -> B): NonEmptyCollection<B>
}

public fun <E> NonEmptyCollection<E>.toNonEmptySet(): NonEmptySet<E> =
  toNonEmptySetOrNull()!!

public fun <E> NonEmptyCollection<E>.toNonEmptyList(): NonEmptyList<E> =
  toNonEmptyListOrNull()!!
