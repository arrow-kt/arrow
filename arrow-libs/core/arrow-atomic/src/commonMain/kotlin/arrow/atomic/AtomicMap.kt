package arrow.atomic

public expect class AtomicMap<K, V> public constructor() : Map<K, V> {
  /**
   * If the specified [key] is not already associated with a value, associates it with the given [value].
   * Otherwise, applies the [combine] value to merge those.
   */
  public fun merge(key: K, value: V, combine: (old: V, new: V) -> V)

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

  /**
   * If the specified [key] is not already associated with a value, associates it with the given [value].
   * Otherwise, applies the [combine] value to merge those.
   */
  public fun merge(key: K, value: V, combine: (old: V, new: V) -> V) {
    while (true) {
      val old = map.get()
      val newValue =
        if (old.containsKey(key)) combine(unwrapNull(old.getValue(key)), value) else value
      if (map.compareAndSet(old, old + Pair(key, newValue ?: Null)))
        return
    }
  }

  /**
   * Replaces the specified [key] with [newValue], only if it was mapped to the given [oldValue].
   */
  public fun replace(key: K, oldValue: V, newValue: V) {
    while (true) {
      val old = map.get()
      if (old[key] == (oldValue ?: Null)) {
        if (map.compareAndSet(old, old + Pair(key, newValue ?: Null)))
          return
      }
    }
  }

  /**
   * Removes the specified [key] only it mapped to the given [value].
   */
  public fun remove(key: K, value: V) {
    while (true) {
      val old = map.get()
      if (old[key] == (value ?: Null)) {
        if (map.compareAndSet(old, old - key))
          return
      }
    }
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

