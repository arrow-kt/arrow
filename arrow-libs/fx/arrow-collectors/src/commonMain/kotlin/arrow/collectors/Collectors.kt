package arrow.collectors

import arrow.atomic.Atomic
import arrow.atomic.AtomicInt
import arrow.atomic.update

/**
 * Library of [Collector]s.
 */
public object Collectors {

  /**
   * Returns always the same value, regardless of the collected flow.
   *
   * @param value Value returns as result of the collection.
   */
  public fun <R> constant(value: R): NonSuspendCollector<Any?, R> = Collector.nonSuspendOf(
    supply = { value },
    accumulate = { _, _ -> },
    finish = { it },
    characteristics = Characteristics.IDENTITY_CONCURRENT_UNORDERED
  )

  /**
   * Counts the number of elements in the flow.
   */
  public val length: NonSuspendCollector<Any?, Int> = Collector.nonSuspendOf(
    supply = { AtomicInt(0) },
    accumulate = { current, _ -> val _ = current.incrementAndGet() },
    finish = AtomicInt::get,
    characteristics = Characteristics.CONCURRENT_UNORDERED
  )

  /**
   * Sum of all the values in the flow.
   */
  public val sum: NonSuspendCollector<Int, Int> =
    intReducer({ 0 }, Int::plus)

  public fun intReducer(
    initial: () -> Int, combine: (Int, Int) -> Int,
  ): NonSuspendCollector<Int, Int> = Collector.nonSuspendOf(
    supply = { AtomicInt(initial()) },
    accumulate = { current, value -> current.update { combine(it, value) } },
    finish = AtomicInt::get,
  )

  public fun <M> reducer(
    initial: () -> M, combine: (M, M) -> M, unordered: Boolean = false,
  ): NonSuspendCollector<M, M> = Collector.nonSuspendOf(
    supply = { Atomic(initial()) },
    accumulate = { current, value -> current.update { combine(it, value) } },
    finish = Atomic<M>::get,
    characteristics = if (unordered) Characteristics.CONCURRENT_UNORDERED else emptySet()
  )

  private data object BestByNotInitialized

  /**
   * Returns the "best" value from the value.
   *
   * @param selectNew Decides whether the new value is "better" than the previous best.
   */
  @Suppress("UNCHECKED_CAST")
  public fun <M> bestBy(
    selectNew: (old: M, new: M) -> Boolean,
  ): NonSuspendCollector<M, M?> = Collector.nonSuspendOf<Atomic<Any?>, M, M?>(
    supply = { Atomic(BestByNotInitialized) },
    accumulate = { current, value ->
      current.update { old ->
        if (old == BestByNotInitialized) {
          value
        } else {
          old as M
          if (selectNew(old, value)) value else old
        }
      }
    },
    finish = { current ->
      when (val result = current.get()) {
        BestByNotInitialized -> null
        else -> result as M
      }
    },
    characteristics = Characteristics.CONCURRENT_UNORDERED
  )

  /**
   * Collects all the values in a list, in the order in which they are emitted.
   */
  @Suppress("UNCHECKED_CAST")
  public fun <T> list(): NonSuspendCollector<T, List<T>> =
    _list as NonSuspendCollector<T, List<T>>

  /**
   * Collects all the values in a set.
   */
  @Suppress("UNCHECKED_CAST")
  public fun <T> set(): NonSuspendCollector<T, Set<T>> =
    _set as NonSuspendCollector<T, Set<T>>

  /**
   * Collects all the values in a map.
   */
  public fun <K, V> mapFromEntries(): NonSuspendCollector<Map.Entry<K, V>, Map<K, V>> =
    map<K, V>().contramapNonSuspend { (k, v) -> k to v }

  /**
   * Collects all the values in a map.
   */
  @Suppress("UNCHECKED_CAST")
  public fun <K, V> map(): NonSuspendCollector<Pair<K, V>, Map<K, V>> =
    _map as NonSuspendCollector<Pair<K, V>, Map<K, V>>

  /* These Collectors can be cached and casted accordingly */

  private val _list: NonSuspendCollector<Any?, List<Any?>> = Collector.nonSuspendOf(
    supply = { mutableListOf() },
    accumulate = MutableList<Any?>::add,
    finish = { it },
    characteristics = Characteristics.IDENTITY
  )

  private val _set: NonSuspendCollector<Any?, Set<Any?>> = Collector.nonSuspendOf(
    supply = ::mutableSetOf,
    accumulate = MutableSet<Any?>::add,
    finish = { it },
    characteristics = Characteristics.IDENTITY_UNORDERED
  )

  private val _map: NonSuspendCollector<Pair<Any?, Any?>, Map<Any?, Any?>> = Collector.nonSuspendOf(
    supply = { mutableMapOf<Any?, Any?>() },
    accumulate = { current, (k, v) -> current[k] = v },
    finish = { it },
    characteristics = Characteristics.IDENTITY_UNORDERED
  )
}
