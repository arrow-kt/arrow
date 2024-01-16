package arrow.collectors

/**
 * Combines two [Collector]s by performing the phases
 * of each of them in parallel.
 *
 * @param x First [Collector]
 * @param y Second [Collector]
 * @param combine Function that combines the end results
 */
public fun <A, R, S, V> zip(
  x: Collector<A, R>,
  y: Collector<A, S>,
  combine: suspend (R, S) -> V,
): Collector<A, V> = x.zip(y, combine)

/**
 * Combines two [NonSuspendCollector]s by performing the phases
 * of each of them in parallel.
 *
 * @param x First [NonSuspendCollector]
 * @param y Second [NonSuspendCollector]
 * @param combine Function that combines the end results
 */
public fun <A, R, S, V> zip(
  x: NonSuspendCollector<A, R>,
  y: NonSuspendCollector<A, S>,
  combine: (R, S) -> V,
): NonSuspendCollector<A, V> = x.zipNonSuspend(y, combine)

/**
 * Combines three [Collector]s by performing the phases
 * of each of them in parallel.
 *
 * @param x First [Collector]
 * @param y Second [Collector]
 * @param z Third [Collector]
 * @param combine Function that combines the end results
 */
public fun <A, R, S, T, V> zip(
  x: Collector<A, R>,
  y: Collector<A, S>,
  z: Collector<A, T>,
  combine: suspend (R, S, T) -> V,
): Collector<A, V> = x.zip(y).zip(z) { (a, b), c -> combine(a, b, c) }

/**
 * Combines three [NonSuspendCollector]s by performing the phases
 * of each of them in parallel.
 *
 * @param x First [NonSuspendCollector]
 * @param y Second [NonSuspendCollector]
 * @param z Third [NonSuspendCollector]
 * @param combine Function that combines the end results
 */
public fun <A, R, S, T, V> zip(
  x: NonSuspendCollector<A, R>,
  y: NonSuspendCollector<A, S>,
  z: NonSuspendCollector<A, T>,
  combine: (R, S, T) -> V,
): NonSuspendCollector<A, V> = x.zip(y).zipNonSuspend(z) { (a, b), c -> combine(a, b, c) }
