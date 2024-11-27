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

/**
 * Combines four [Collector]s by performing the phases
 * of each of them in parallel.
 *
 * @param c1 First [Collector]
 * @param c2 Second [Collector]
 * @param c3 Third [Collector]
 * @param c4 Fourth [Collector]
 * @param combine Function that combines the end results
 */
public fun <A, C1, C2, C3, C4, R> zip(
  c1: Collector<A, C1>,
  c2: Collector<A, C2>,
  c3: Collector<A, C3>,
  c4: Collector<A, C4>,
  combine: suspend (C1, C2, C3, C4) -> R,
): Collector<A, R> = c1.zip(c2).zip(c3).zip(c4) { (ab, c), d -> combine(ab.first, ab.second, c, d) }

/**
 * Combines four [NonSuspendCollector]s by performing the phases
 * of each of them in parallel.
 *
 * @param c1 First [NonSuspendCollector]
 * @param c2 Second [NonSuspendCollector]
 * @param c3 Third [NonSuspendCollector]
 * @param c4 Fourth [NonSuspendCollector]
 * @param combine Function that combines the end results
 */
public fun <A, C1, C2, C3, C4, R> zip(
  c1: NonSuspendCollector<A, C1>,
  c2: NonSuspendCollector<A, C2>,
  c3: NonSuspendCollector<A, C3>,
  c4: NonSuspendCollector<A, C4>,
  combine: (C1, C2, C3, C4) -> R,
): NonSuspendCollector<A, R> = c1.zip(c2).zip(c3).zipNonSuspend(c4) { (ab, c), d -> combine(ab.first, ab.second, c, d) }

/**
 * Combines five [Collector]s by performing the phases
 * of each of them in parallel.
 *
 * @param c1 First [Collector]
 * @param c2 Second [Collector]
 * @param c3 Third [Collector]
 * @param c4 Fourth [Collector]
 * @param c5 Fifth [Collector]
 * @param combine Function that combines the end results
 */
public fun <A, C1, C2, C3, C4, C5, R> zip(
  c1: Collector<A, C1>,
  c2: Collector<A, C2>,
  c3: Collector<A, C3>,
  c4: Collector<A, C4>,
  c5: Collector<A, C5>,
  combine: suspend (C1, C2, C3, C4, C5) -> R,
): Collector<A, R> = c1.zip(c2).zip(c3).zip(c4).zip(c5) { (abc, d), e -> combine(abc.first.first, abc.first.second, abc.second, d, e) }

/**
 * Combines four [NonSuspendCollector]s by performing the phases
 * of each of them in parallel.
 *
 * @param c1 First [NonSuspendCollector]
 * @param c2 Second [NonSuspendCollector]
 * @param c3 Third [NonSuspendCollector]
 * @param c4 Fourth [NonSuspendCollector]
 * @param c5 Fifth [NonSuspendCollector]
 * @param combine Function that combines the end results
 */
public fun <A, C1, C2, C3, C4, C5, R> zip(
  c1: NonSuspendCollector<A, C1>,
  c2: NonSuspendCollector<A, C2>,
  c3: NonSuspendCollector<A, C3>,
  c4: NonSuspendCollector<A, C4>,
  c5: NonSuspendCollector<A, C5>,
  combine: (C1, C2, C3, C4, C5) -> R,
): NonSuspendCollector<A, R> = c1.zip(c2).zip(c3).zip(c4).zipNonSuspend(c5) { (abc, d), e -> combine(abc.first.first, abc.first.second, abc.second, d, e) }
