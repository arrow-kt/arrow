package arrow.collectors

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
    public val IDENTITY: Set<Characteristics> = setOf(IDENTITY_FINISH)
    public val IDENTITY_UNORDERED: Set<Characteristics> = setOf(IDENTITY_FINISH, UNORDERED)
    public val CONCURRENT_UNORDERED: Set<Characteristics> = setOf(CONCURRENT, UNORDERED)
    public val IDENTITY_CONCURRENT: Set<Characteristics> = setOf(IDENTITY_FINISH, CONCURRENT)
    public val IDENTITY_CONCURRENT_UNORDERED: Set<Characteristics> = setOf(IDENTITY_FINISH, CONCURRENT, UNORDERED)
  }
}


public typealias Collector<Value, Result> = CollectorI<*, Value, Result>

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
public interface CollectorI<InternalAccumulator, in Value, out Result> {
  public val characteristics: Set<Characteristics>
  public suspend fun supply(): InternalAccumulator
  public suspend fun accumulate(current: InternalAccumulator, value: Value)
  public suspend fun finish(current: InternalAccumulator): Result

  public companion object {
    /**
     * Constructs a new [Collector] from its components
     */
    public fun <InternalAccumulator, Value, Result> of(
      supply: suspend () -> InternalAccumulator,
      accumulate: suspend (current: InternalAccumulator, value: Value) -> Unit,
      finish: suspend (current: InternalAccumulator) -> Result,
      characteristics: Set<Characteristics> = setOf(),
    ): Collector<Value, Result> = object : CollectorI<InternalAccumulator, Value, Result> {
      override val characteristics: Set<Characteristics> = characteristics
      override suspend fun supply(): InternalAccumulator = supply()
      override suspend fun accumulate(current: InternalAccumulator, value: Value) {
        accumulate(current, value)
      }

      override suspend fun finish(current: InternalAccumulator): Result = finish(current)
    }
  }

  /**
   * Performs additional work during the finalization phase,
   * by applying a function to the end result.
   *
   * @param transform Additional function to apply to the end result.
   */
  // functor over R
  public fun <S> map(
    transform: suspend (Result) -> S,
  ): Collector<Value, S> = of(
    supply = this::supply,
    accumulate = this::accumulate,
    finish = { current -> transform(this.finish(current)) },
    characteristics = this.characteristics,
  )

  /**
   * Applies a function over each element in the data source,
   * before giving it to the collector.
   *
   * @param transform Function to apply to each value
   */
  // contravariant functor over T
  public fun <P> contramap(
    transform: suspend (P) -> Value,
  ): Collector<P, Result> = of(
    supply = this::supply,
    accumulate = { current, value -> this.accumulate(current, transform(value)) },
    finish = this::finish,
    characteristics = this.characteristics,
  )

  /**
   * Combines two [Collector]s by performing the phases
   * of each of them in parallel.
   *
   * @param other [Collector] to combine with [this]
   */
  // applicative
  public fun <B, S> zip(
    other: CollectorI<B, @UnsafeVariance Value, S>,
  ): Collector<Value, Pair<Result, S>> =
    this.zip(other, ::Pair)

  /**
   * Combines two [Collector]s by performing the phases
   * of each of them in parallel, and then combining
   * the end result in a final step.
   *
   * @param other [Collector] to combine with [this]
   * @param combine Function that combines the end results
   */
  public fun <B, S, V> zip(
    other: CollectorI<B, @UnsafeVariance Value, S>,
    combine: (Result, S) -> V,
  ): Collector<Value, V> = of(
    supply = { Pair(this.supply(), other.supply()) },
    accumulate = { (currentThis, currentOther), value ->
      this.accumulate(currentThis, value)
      other.accumulate(currentOther, value)
    },
    finish = {(currentThis, currentOther) ->
      combine(this.finish(currentThis), other.finish(currentOther))
    },
    characteristics = this.characteristics,
  )
}
