package arrow.core

/**
 * Common interface for collections that always have
 * at least one element (available from [head]).
 */
@SubclassOptInRequired(PotentiallyUnsafeNonEmptyOperation::class)
public interface NonEmptyCollection<out E> : Collection<E> {
  override fun isEmpty(): Boolean = false
  public val head: E get() = first()

  public operator fun plus(element: @UnsafeVariance E): NonEmptyCollection<E>
  public operator fun plus(elements: Iterable<@UnsafeVariance E>): NonEmptyCollection<E>

  public fun toNonEmptySet(): NonEmptySet<E> = buildNonEmptySet(size) {
    addAll(this@NonEmptyCollection)
    this
  }

  public fun toNonEmptyList(): NonEmptyList<E> = buildNonEmptyList(size) {
    addAll(this@NonEmptyCollection)
    this
  }

  // These functions take precedence over the extensions in [Collection].
  // This way non-emptiness is tracked by the type system.

  public fun firstOrNull(): E = head
  public fun lastOrNull(): E = last()

  public fun distinct(): NonEmptyList<E> = toNonEmptySet().toNonEmptyList()
  public fun <K> distinctBy(selector: (E) -> K): NonEmptyList<E> = buildNonEmptyList(size) {
    add(head) // head is always distinct
    val seen = hashSetOf<K>()
    var isFirst = true
    for (e in this@NonEmptyCollection) {
      if (seen.add(selector(e)) && !isFirst) add(e)
      isFirst = false
    }
    this
  }
  public fun <T> flatMap(transform: (E) -> NonEmptyCollection<T>): NonEmptyList<T> = buildNonEmptyList(size) {
    val iterator = this@NonEmptyCollection.iterator()
    do addAll(transform(iterator.next())) while (iterator.hasNext())
    this
  }
  public fun <T> map(transform: (E) -> T): NonEmptyList<T> = buildNonEmptyList(size) {
    val iterator = this@NonEmptyCollection.iterator()
    do add(transform(iterator.next())) while (iterator.hasNext())
    this
  }
  public fun <T> mapIndexed(transform: (index:Int, E) -> T): NonEmptyList<T> = buildNonEmptyList(size) {
    var i = 0
    val iterator = this@NonEmptyCollection.iterator()
    do add(transform(i++, iterator.next())) while (iterator.hasNext())
    this
  }
  public fun <T> zip(other: NonEmptyCollection<T>): NonEmptyCollection<Pair<E, T>> = buildNonEmptyList(minOf(size, other.size)) {
    val first = this@NonEmptyCollection.iterator()
    val second = other.iterator()
    do add(first.next() to second.next()) while (first.hasNext() && second.hasNext())
    this
  }
}

/**
 * Marks operations which may break non-emptiness invariants if used wrong.
 * In most cases, it involves using mutable collections without copying the elements first.
 */
@RequiresOptIn
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CONSTRUCTOR)
public annotation class PotentiallyUnsafeNonEmptyOperation
