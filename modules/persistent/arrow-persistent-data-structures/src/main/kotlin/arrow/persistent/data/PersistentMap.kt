package arrow.persistent.data

import arrow.core.Option
import arrow.higherkind
import arrow.persistent.internal.HashArrayMappedTrie

/**
 * A [PersistentMap] is an immutable [Map] which implements structural sharing.
 */
@higherkind
data class PersistentMap<K, V>(private val map: HashArrayMappedTrie<K, V>) {

  val isEmpty: Boolean = map.isEmpty
  val size: Int = map.size

  operator fun get(key: K): Option<V> = map[key]

  fun containsKey(key: K): Boolean = map.containsKey(key)

  fun put(key: K, value: V): PersistentMap<K, V> {
    return PersistentMap(map.put(key, value))
  }

  fun remove(key: K): PersistentMap<K, V> {
    return PersistentMap(map.remove(key))
  }
}
