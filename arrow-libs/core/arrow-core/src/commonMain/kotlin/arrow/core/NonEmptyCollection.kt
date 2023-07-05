package arrow.core

/**
 * Common interface for collections that always have
 * at least one element (available from [head]).
 */
public interface NonEmptyCollection<out A> : Collection<A> {
  override fun isEmpty(): Boolean = false
  public val head: A

  public operator fun plus(element: @UnsafeVariance A): NonEmptyCollection<A>
  public operator fun plus(elements: Iterable<@UnsafeVariance A>): NonEmptyCollection<A>

  public fun toNonEmptySet(): NonEmptySet<A> = toNonEmptySetOrNull()!!
  public fun toNonEmptyList(): NonEmptyList<A> = toNonEmptyListOrNull()!!

  // These functions take precedence over the extensions in [Collection].
  // This way non-emptiness is tracked by the type system.

  public fun firstOrNull(): A = head
  public fun lastOrNull(): A

  public fun distinct(): NonEmptyList<A> =
    delegate { it.distinct() }
  public fun <K> distinctBy(selector: (A) -> K): NonEmptyList<A> =
    delegate { it.distinctBy(selector) }
  public fun <B> flatMap(transform: (A) -> NonEmptyCollection<B>): NonEmptyList<B> =
    delegate { it.flatMap(transform) }
  public fun <B> map(transform: (A) -> B): NonEmptyList<B> =
    delegate { it.map(transform) }
  public fun <B> mapIndexed(transform: (index:Int, A) -> B): NonEmptyList<B> =
    delegate { it.mapIndexed(transform) }
  public fun <B> zip(other: NonEmptyCollection<B>): NonEmptyCollection<Pair<A, B>> =
    delegate { it.zip(other) }

  /**
   * Convenience method which delegates the implementation to [Collection],
   * and wraps the resulting [List] as a non-empty one.
   */
  private inline fun <R> delegate(crossinline f: (Collection<A>) -> List<R>): NonEmptyList<R> =
    f(this as Collection<A>).toNonEmptyListOrNull()!!
}
