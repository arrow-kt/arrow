package arrow.collectors

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentHashMap.KeySetView
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
public fun <K, V> Collectors.concurrentMap(): Collector<Pair<K, V>, ConcurrentMap<K, V>> =
  object : CollectorI<ConcurrentHashMap<K, V>, Pair<K, V>, ConcurrentMap<K, V>> {
    override val characteristics: Set<Characteristics> = Characteristics.IDENTITY_CONCURRENT_UNORDERED
    override suspend fun supply(): ConcurrentHashMap<K, V> = ConcurrentHashMap()
    override suspend fun accumulate(current: ConcurrentHashMap<K, V>, value: Pair<K, V>) {
      current[value.first] = value.second
    }

    override suspend fun finish(current: ConcurrentHashMap<K, V>): ConcurrentMap<K, V> = current
  }

/**
 * Collects all the values in a set.
 *
 * This [Collector] supports concurrency,
 * so it's potential faster than [Collectors.set].
 */
@Suppress("UnusedReceiverParameter")
public fun <T> Collectors.concurrentSet(): Collector<T, Set<T>> =
  object : CollectorI<KeySetView<T, Unit>, T, Set<T>> {
    override val characteristics: Set<Characteristics> = Characteristics.IDENTITY_CONCURRENT_UNORDERED
    override suspend fun supply(): KeySetView<T, Unit> = ConcurrentHashMap<T, Unit>().keySet(Unit)
    override suspend fun accumulate(current: KeySetView<T, Unit>, value: T) {
      current.add(value)
    }

    override suspend fun finish(current: KeySetView<T, Unit>): Set<T> = current
  }
