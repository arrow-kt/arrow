package arrow.fx.internal

import arrow.core.Tuple2
import arrow.core.getOption
import arrow.core.toT

/**
 * A Map which tracks the insertion order of entries, so that entries may be
 * traversed in the order they were inserted.
 */
internal class LinkedMap<K, V>(
  private val entries: Map<K, Tuple2<V, Long>>,
  private val insertionOrder: Map<Long, K>,
  private val nextId: Long
) {

  /** Returns `true` if this map is empty, or `false` otherwise. */
  fun isEmpty(): Boolean =
    entries.isEmpty()

  fun isNotEmpty(): Boolean =
    !isEmpty()

  /** The keys in this map, in the order they were added. */
  val keys: Iterable<K>
    get() = insertionOrder.values

  /** The values in this map, in the order they were added. */
  val values: Iterable<V>
    get() = keys.map { k -> entries.getValue(k).a }

  /** Returns a new map with the supplied key/value added. */
  operator fun plus(t: Tuple2<K, V>): LinkedMap<K, V> {
    val insertionOrderOldRemoved = entries.getOption(t.a).map { it.b }
      .fold(ifEmpty = { insertionOrder }, ifSome = { id -> insertionOrder - id })

    return LinkedMap(
      entries + (t.a to (t.b toT nextId)),
      insertionOrderOldRemoved + (nextId to t.a),
      nextId + 1
    )
  }

  /** Removes the element at the specified key. */
  operator fun minus(k: K): LinkedMap<K, V> =
    LinkedMap(
      entries - k,
      entries.getOption(k).map { it.b }
        .fold(ifEmpty = { insertionOrder }, ifSome = { id -> insertionOrder - id }),
      nextId
    )

  /** Pulls the first value from this `LinkedMap`, in FIFO order. */
  fun dequeue(): Tuple2<V, LinkedMap<K, V>> {
    val k = insertionOrder.entries.first().value
    return entries.getValue(k).a toT (this - k)
  }

  companion object {
    fun <K, V> empty(): LinkedMap<K, V> =
      LinkedMap(emptyMap(), emptyMap(), 0)
  }
}
