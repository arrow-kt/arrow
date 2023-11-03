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
  combine: (R, S) -> V,
): Collector<A, V> = x.zip(y, combine)

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
  combine: (R, S, T) -> V,
): Collector<A, V> = x.zip(y).zip(z) { (a, b), c -> combine(a, b, c) }
