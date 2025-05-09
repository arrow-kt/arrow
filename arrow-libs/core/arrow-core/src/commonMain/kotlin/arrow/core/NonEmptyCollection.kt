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
  public fun <T> flatMap(transform: (E) -> NonEmptyCollection<T>): NonEmptyList<T> =
    delegate { it.flatMap(transform) }
  public fun <T> map(transform: (E) -> T): NonEmptyList<T> =
    delegate { it.map(transform) }
  public fun <T> mapIndexed(transform: (index:Int, E) -> T): NonEmptyList<T> =
    delegate { it.mapIndexed(transform) }
  public fun <T> zip(other: NonEmptyCollection<T>): NonEmptyCollection<Pair<E, T>> =
    delegate { it.zip(other) }

  /**
   * Convenience method which delegates the implementation to [Collection],
   * and wraps the resulting [List] as a non-empty one.
   */
  private inline fun <T> delegate(crossinline f: (Collection<E>) -> List<T>): NonEmptyList<T> =
    f(this as Collection<E>).toNonEmptyListOrNull()!!
}

/**
 * Marks operations which may break non-emptiness invariants if used wrong.
 * In most cases, it involves using mutable collections without copying the elements first.
 */
@RequiresOptIn
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FUNCTION)
public annotation class PotentiallyUnsafeNonEmptyOperation
