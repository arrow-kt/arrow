package arrow.core

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

@JvmInline
public value class ConcurrentMapMemoizationCache<K, V>(
  private val cache: ConcurrentMap<K, V> = ConcurrentHashMap(),
): MemoizationCache<K, V> {
  override fun get(key: K): V? = cache.get(key)
  override fun set(key: K, value: V): V = when (key) {
    null -> value
    else -> cache.putIfAbsent(key, value) ?: value
  }
}
