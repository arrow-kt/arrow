package arrow.atomic

public expect class AtomicMap<K, V> public constructor() : Map<K, V> {
  /**
   * If the specified [key] is not already associated with a value, associates it with the given [value].
   * Otherwise, applies the [combine] value to merge those.
   */
  public fun merge(key: K, value: V, combine: (oldValue: V, newValue: V) -> V)

  /**
   * Replaces the specified [key] with [newValue], only if it was mapped to the given [oldValue].
   */
  public fun replace(key: K, oldValue: V, newValue: V)

  /**
   * Removes the specified [key] only if it was mapped to the given [value].
   */
  public fun remove(key: K, value: V)

  /**
   * Maps the [key] to the [value], only if there was no previous value associated with it.
   */
  public fun putIfAbsent(key: K, value: V)

  /**
   * Replaces the existing value of [key] with the new [value].
   */
  public fun replace(key: K, value: V)
}

public class DefaultAtomicMap<K, V>: Map<K, V> {
  // used to signal that we have a 'null' as value
  public object Null

  @Suppress("UNCHECKED_CAST")
  private inline fun <V> unwrapNull(x: Any): V =
    (if (x is Null) null else x) as V

  private val map: Atomic<Map<K, Any>> = Atomic(mapOf())

  private inline fun loop(crossinline block: (Map<K, Any>) -> Map<K, Any>) {
    while (true) {
      val oldMap = map.get()
      val newMap = block(oldMap)
      if (oldMap == newMap || map.compareAndSet(oldMap, newMap))
        return
    }
  }

  /**
   * If the specified [key] is not already associated with a value, associates it with the given [value].
   * Otherwise, applies the [combine] value to merge those.
   */
  public fun merge(key: K, value: V, combine: (oldValue: V, newValue: V) -> V): Unit = loop { oldMap ->
    val newValue =
      if (oldMap.containsKey(key)) combine(unwrapNull(oldMap.getValue(key)), value) else value
    oldMap + Pair(key, newValue ?: Null)
  }

  /**
   * Replaces the specified [key] with [newValue], only if it was mapped to the given [oldValue].
   */
  public fun replace(key: K, oldValue: V, newValue: V): Unit = loop { oldMap ->
    if (oldMap[key] == (oldValue ?: Null))
      oldMap + Pair(key, newValue ?: Null)
    else
      oldMap
  }

  /**
   * Removes the specified [key] only it mapped to the given [value].
   */
  public fun remove(key: K, value: V): Unit = loop { oldMap ->
    if (oldMap[key] == (value ?: Null))
      oldMap - key
    else
      oldMap
  }

  /**
   * Maps the [key] to the [value], only if there was no previous value associated with it.
   */
  public fun putIfAbsent(key: K, value: V): Unit = merge(key, value) { old, _ -> old }

  /**
   * Replaces the existing value of [key] with the new [value].
   */
  public fun replace(key: K, value: V): Unit = merge(key, value) { _, new -> new }

  override val size: Int
    get() = map.get().size
  override fun isEmpty(): Boolean =
    map.get().isEmpty()

  override val entries: Set<Map.Entry<K, V>>
    get() = map.get().entries.map { entry -> object: Map.Entry<K, V> {
      override val key: K = entry.key
      override val value: V = unwrapNull(entry.value)
    } }.toSet()
  override val keys: Set<K>
    get() = map.get().keys
  override val values: Collection<V>
    get() = map.get().values.map(::unwrapNull)

  override fun get(key: K): V? = map.get()[key]?.let(::unwrapNull)
  override fun containsValue(value: V): Boolean = map.get().containsValue(value ?: Null)
  override fun containsKey(key: K): Boolean = map.get().containsKey(key)

}

