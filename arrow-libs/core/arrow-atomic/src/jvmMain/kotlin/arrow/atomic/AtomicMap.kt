package arrow.atomic

import java.util.concurrent.ConcurrentHashMap

public actual class AtomicMap<K, V>: Map<K, V> {
  // used to signal that we have a 'null' as value
  public object Null

  private val map: ConcurrentHashMap<K, Any> = ConcurrentHashMap()

  @Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
  private inline fun <V> unwrapNull(x: Any): V =
    (if (x is Null) null else x) as V

  /**
   * If the specified [key] is not already associated with a value, associates it with the given [value].
   * Otherwise, applies the [combine] value to merge those.
   */
  public actual fun merge(key: K, value: V, combine: (oldValue: V, newValue: V) -> V) {
    map.merge(key, value ?: Null) { old, new ->
      combine(unwrapNull(old), unwrapNull(new)) ?: Null
    }
  }

  /**
   * Replaces the specified [key] with [newValue], only if it was mapped to the given [oldValue].
   */
  public actual fun replace(key: K, oldValue: V, newValue: V) {
    map.replace(key, oldValue ?: Null, newValue ?: Null)
  }

  /**
   * Removes the specified [key] only it mapped to the given [value].
   */
  public actual fun remove(key: K, value: V) {
    map.remove(key, value ?: Null)
  }

  /**
   * Maps the [key] to the [value], only if there was no previous value associated with it.
   */
  public actual fun putIfAbsent(key: K, value: V) {
    map.putIfAbsent(key, value ?: Null)
  }

  /**
   * Replaces the existing value of [key] with the new [value].
   */
  public actual fun replace(key: K, value: V) {
    map.replace(key, value ?: Null)
  }

  override val size: Int
    get() = map.size
  override fun isEmpty(): Boolean = map.isEmpty()

  override val entries: Set<Map.Entry<K, V>>
    get() = map.entries.map { entry -> object: Map.Entry<K, V> {
      override val key: K = entry.key
      override val value: V = unwrapNull(entry.value)
    } }.toSet()
  override val keys: Set<K>
    get() = map.keys
  override val values: Collection<V>
    get() = map.values.map(::unwrapNull)

  override fun get(key: K): V? = map[key]?.let(::unwrapNull)
  override fun containsValue(value: V): Boolean = map.containsValue(value ?: Null)
  override fun containsKey(key: K): Boolean = map.containsKey(key)
}
