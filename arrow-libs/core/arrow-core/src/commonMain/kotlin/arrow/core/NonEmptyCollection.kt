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

  public fun toNonEmptySet(): NonEmptySet<E> = toNonEmptySetOrNull()!!
  public fun toNonEmptyList(): NonEmptyList<E> = toNonEmptyListOrNull()!!

  // These functions take precedence over the extensions in [Collection].
  // This way non-emptiness is tracked by the type system.

  public fun firstOrNull(): E = head
  public fun lastOrNull(): E

  public fun distinct(): NonEmptyList<E> =
    delegate { it.distinct() }
  public fun <K> distinctBy(selector: (E) -> K): NonEmptyList<E> =
    delegate { it.distinctBy(selector) }
  public fun <B> flatMap(transform: (E) -> NonEmptyCollection<B>): NonEmptyList<B> =
    delegate { it.flatMap(transform) }
  public fun <B> map(transform: (E) -> B): NonEmptyList<B> =
    delegate { it.map(transform) }
  public fun <B> mapIndexed(transform: (index:Int, E) -> B): NonEmptyList<B> =
    delegate { it.mapIndexed(transform) }
  public fun <B> zip(other: NonEmptyCollection<B>): NonEmptyCollection<Pair<E, B>> =
    delegate { it.zip(other) }

  /**
   * Convenience method which delegates the implementation to [Collection],
   * and wraps the resulting [List] as a non-empty one.
   */
  private inline fun <R> delegate(crossinline f: (Collection<E>) -> List<R>): NonEmptyList<R> =
    f(this as Collection<E>).toNonEmptyListOrNull()!!
}
