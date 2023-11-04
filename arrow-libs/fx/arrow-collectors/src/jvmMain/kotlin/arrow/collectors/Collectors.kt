package arrow.collectors

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

/**
 * Collects all the values in a map.
 *
 * This [Collector] supports concurrency,
 * so it's potential faster than [Collectors.mapFromEntries].
 */
@Suppress("UnusedReceiverParameter")
public fun <K, V> Collectors.concurrentMapFromEntries(): Collector<Map.Entry<K, V>, ConcurrentMap<K, V>> =
  Collectors.concurrentMap<K, V>().contramap { (k, v) -> k to v }

/**
 * Collects all the values in a map.
 *
 * This [Collector] supports concurrency,
 * so it's potential faster than [Collectors.map].
 */
@Suppress("UnusedReceiverParameter")
public fun <K, V> Collectors.concurrentMap(): Collector<Pair<K, V>, ConcurrentMap<K, V>> = Collector.of(
  supply = { ConcurrentHashMap<K, V>() },
  accumulate = { current, (k, v) -> current[k] = v },
  finish = { it },
  characteristics = Characteristics.IDENTITY_CONCURRENT_UNORDERED
)

/**
 * Collects all the values in a set.
 *
 * This [Collector] supports concurrency,
 * so it's potential faster than [Collectors.set].
 */
@Suppress("UnusedReceiverParameter")
public fun <T> Collectors.concurrentSet(): Collector<T, Set<T>> = Collector.of(
  supply = { ConcurrentHashMap<T, Unit>().keySet(Unit) },
  accumulate = ConcurrentHashMap.KeySetView<T, Unit>::add,
  finish = { it },
  characteristics = Characteristics.IDENTITY_CONCURRENT_UNORDERED
)
