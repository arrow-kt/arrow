package arrow.collectors

// https://hackage.haskell.org/package/foldl-1.4.15/docs/Control-Foldl.html

/**
 * Defines how the [collect] function may run the collector.
 */
public enum class Characteristics {
  /**
   * The flow may be collected using several workers.
   */
  CONCURRENT,

  /**
   * The final step of the collector simply returns the accumulator.
   * That means that final call may be replaced by a cast.
   */
  IDENTITY_FINISH,

  /**
   * The order in which elements are accumulated doesn't matter for the end result.
   */
  UNORDERED;

  public companion object {
    public val CONCURRENT_UNORDERED: Set<Characteristics> = setOf(CONCURRENT, UNORDERED)
    public val IDENTITY_CONCURRENT: Set<Characteristics> = setOf(IDENTITY_FINISH, CONCURRENT)
    public val IDENTITY_CONCURRENT_UNORDERED: Set<Characteristics> = setOf(IDENTITY_FINISH, CONCURRENT, UNORDERED)
  }
}


public typealias Collector<T, R> = CollectorI<*, T, R>

/**
 * A [Collector] accumulates information from elements
 * coming from some data source, usually a [kotlinx.coroutines.flow.Flow] or [Iterable].
 *
 * The accumulation is done in three phases:
 * - Initialization of some (mutable) accumulator ([supply])
 * - Updating the accumulator with each value ([accumulate])
 * - Finalize the work, and extract the final result ([finish])
 *
 * This interface is heavily influenced by
 * Java's [`Collector`](https://docs.oracle.com/javase/8/docs/api/java/util/stream/Collector.html)
 * and Haskell's [`foldl` library](https://hackage.haskell.org/package/foldl/docs/Control-Foldl.html).
 */
public interface CollectorI<A, in T, out R> {
  public val characteristics: Set<Characteristics>
  public suspend fun supply(): A
  public suspend fun accumulate(current: A, value: T)
  public suspend fun finish(current: A): R

  /**
   * Performs additional work during the finalization phase,
   * by applying a function to the end result.
   *
   * @param finish Additional function to apply to the end result.
   */
  // functor over R
  public fun <S> map(
    finish: suspend (R) -> S,
  ): Collector<T, S> {
    val me = this
    return object : CollectorI<A, T, S> {
      override val characteristics: Set<Characteristics> =
        me.characteristics

      override suspend fun supply(): A =
        me.supply()

      override suspend fun accumulate(current: A, value: T) =
        me.accumulate(current, value)

      override suspend fun finish(current: A): S =
        finish(me.finish(current))
    }
  }

  /**
   * Applies a function over each element in the data source,
   * before giving it to the collector.
   *
   * @param transform Function to apply to each value
   */
  // contravariant functor over T
  public fun <P> contramap(
    transform: suspend (P) -> T,
  ): Collector<P, R> {
    val me = this
    return object : CollectorI<A, P, R> {
      override val characteristics: Set<Characteristics> =
        me.characteristics

      override suspend fun supply(): A =
        me.supply()

      override suspend fun accumulate(current: A, value: P) =
        me.accumulate(current, transform(value))

      override suspend fun finish(current: A): R =
        me.finish(current)
    }
  }

  /**
   * Combines two [Collector]s by performing the phases
   * of each of them in parallel.
   *
   * @param other [Collector] to combine with [this]
   */
  // applicative
  public fun <B, S> zip(
    other: CollectorI<B, @UnsafeVariance T, S>,
  ): Collector<T, Pair<R, S>> =
    this.zip(other, ::Pair)

  /**
   * Combines two [Collector]s by performing the phases
   * of each of them in parallel, and then combining
   * the end result in a final step.
   *
   * @param other [Collector] to combine with [this]
   * @param finish Function that combines the end results
   */
  public fun <B, S, V> zip(
    other: CollectorI<B, @UnsafeVariance T, S>,
    finish: (R, S) -> V,
  ): Collector<T, V> {
    val me = this
    return object : CollectorI<Pair<A, B>, T, V> {
      override val characteristics: Set<Characteristics> =
        me.characteristics.intersect(other.characteristics)

      override suspend fun supply(): Pair<A, B> =
        Pair(me.supply(), other.supply())

      override suspend fun accumulate(current: Pair<A, B>, value: T) {
        me.accumulate(current.first, value)
        other.accumulate(current.second, value)
      }

      override suspend fun finish(current: Pair<A, B>): V =
        finish(me.finish(current.first), other.finish(current.second))
    }
  }
}
