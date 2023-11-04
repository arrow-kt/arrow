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
  public fun <R> constant(value: R): Collector<Any?, R> = Collector.of(
    supply = { value },
    accumulate = { _, _ -> },
    finish = { it },
    characteristics = Characteristics.IDENTITY_CONCURRENT_UNORDERED
  )

  /**
   * Counts the number of elements in the flow.
   */
  public val length: Collector<Any?, Int> = Collector.of(
    supply = { AtomicInt(0) },
    accumulate = { current, _ -> current.incrementAndGet() },
    finish = AtomicInt::get,
    characteristics = Characteristics.CONCURRENT_UNORDERED
  )

  /**
   * Sum of all the values in the flow.
   */
  public val sum: Collector<Int, Int> =
    intReducer({ 0 }, Int::plus)

  public fun intReducer(
    initial: () -> Int, combine: (Int, Int) -> Int,
  ): Collector<Int, Int> = Collector.of(
    supply = { AtomicInt(initial()) },
    accumulate = { current, value -> current.update { combine(it, value) } },
    finish = AtomicInt::get,
  )

  public fun <M> reducer(
    initial: () -> M, combine: (M, M) -> M, unordered: Boolean = false,
  ): Collector<M, M> = Collector.of(
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
  ): Collector<M, M?> = Collector.of<Atomic<Any?>, M, M?>(
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
  public fun <T> list(): Collector<T, List<T>> = _list as Collector<T, List<T>>

  /**
   * Collects all the values in a set.
   */
  @Suppress("UNCHECKED_CAST")
  public fun <T> set(): Collector<T, Set<T>> = _set as Collector<T, Set<T>>

  /**
   * Collects all the values in a map.
   */
  public fun <K, V> mapFromEntries(): Collector<Map.Entry<K, V>, Map<K, V>> =
    map<K, V>().contramap { (k, v) -> k to v }

  /**
   * Collects all the values in a map.
   */
  @Suppress("UNCHECKED_CAST")
  public fun <K, V> map(): Collector<Pair<K, V>, Map<K, V>> = _map as Collector<Pair<K, V>, Map<K, V>>

  /* These Collectors can be cached and casted accordingly */

  private val _list: Collector<Any?, List<Any?>> = Collector.of(
    supply = { mutableListOf() },
    accumulate = MutableList<Any?>::add,
    finish = { it },
    characteristics = Characteristics.IDENTITY
  )

  private val _set: Collector<Any?, Set<Any?>> = Collector.of(
    supply = ::mutableSetOf,
    accumulate = MutableSet<Any?>::add,
    finish = { it },
    characteristics = Characteristics.IDENTITY_UNORDERED
  )

  private val _map: Collector<Pair<Any?, Any?>, Map<Any?, Any?>> = Collector.of(
    supply = { mutableMapOf<Any?, Any?>() },
    accumulate = { current, (k, v) -> current[k] = v },
    finish = { it },
    characteristics = Characteristics.IDENTITY_UNORDERED
  )
}
