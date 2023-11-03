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
  public fun <R> constant(value: R): Collector<Any?, R> =
    object : CollectorI<R, Any?, R> {
      override val characteristics: Set<Characteristics> = Characteristics.IDENTITY_CONCURRENT_UNORDERED
      override suspend fun supply(): R = value
      override suspend fun accumulate(current: R, value: Any?) {}
      override suspend fun finish(current: R): R = current
    }

  /**
   * Counts the number of elements in the flow.
   */
  public val length: Collector<Any?, Int> =
    object : CollectorI<AtomicInt, Any?, Int> {
      override val characteristics: Set<Characteristics> = Characteristics.CONCURRENT_UNORDERED
      override suspend fun supply(): AtomicInt = AtomicInt(0)
      override suspend fun accumulate(current: AtomicInt, value: Any?) {
        current.incrementAndGet()
      }

      override suspend fun finish(current: AtomicInt): Int = current.get()
    }

  /**
   * Sum of all the values in the flow.
   */
  public val sum: Collector<Int, Int> =
    intReducer({ 0 }, Int::plus)

  public fun intReducer(
    initial: () -> Int, combine: (Int, Int) -> Int,
  ): Collector<Int, Int> =
    object : CollectorI<AtomicInt, Int, Int> {
      override val characteristics: Set<Characteristics> = emptySet()
      override suspend fun supply(): AtomicInt = AtomicInt(initial())
      override suspend fun accumulate(current: AtomicInt, value: Int) {
        current.update { combine(it, value) }
      }

      override suspend fun finish(current: AtomicInt): Int = current.get()
    }

  public fun <M> reducer(
    initial: () -> M, combine: (M, M) -> M, unordered: Boolean = false,
  ): Collector<M, M> =
    object : CollectorI<Atomic<M>, M, M> {
      override val characteristics: Set<Characteristics> =
        if (unordered) Characteristics.CONCURRENT_UNORDERED
        else emptySet()

      override suspend fun supply(): Atomic<M> = Atomic(initial())
      override suspend fun accumulate(current: Atomic<M>, value: M) {
        current.update { combine(it, value) }
      }

      override suspend fun finish(current: Atomic<M>): M = current.get()
    }

  /**
   * Returns the "best" value from the value.
   *
   * @param selectNew Decides whether the new value is "better" than the previous best.
   */
  public fun <M> bestBy(
    selectNew: (old: M, new: M) -> Boolean,
  ): Collector<M, M?> =
    object : CollectorI<Atomic<M?>, M, M?> {
      override val characteristics: Set<Characteristics> = Characteristics.CONCURRENT_UNORDERED
      override suspend fun supply(): Atomic<M?> = Atomic(null)
      override suspend fun accumulate(current: Atomic<M?>, value: M) {
        current.update { old ->
          if (old == null) value
          else if (selectNew(old, value)) value
          else old
        }
      }

      override suspend fun finish(current: Atomic<M?>): M? = current.get()
    }

  /**
   * Collects all the values in a list, in the order in which they are emitted.
   */
  public fun <T> list(): Collector<T, List<T>> =
    object : CollectorI<MutableList<T>, T, List<T>> {
      override val characteristics: Set<Characteristics> = setOf(Characteristics.IDENTITY_FINISH)
      override suspend fun supply(): MutableList<T> = mutableListOf()
      override suspend fun accumulate(current: MutableList<T>, value: T) {
        current.add(value)
      }

      override suspend fun finish(current: MutableList<T>): List<T> = current
    }

  /**
   * Collects all the values in a set.
   */
  public fun <T> set(): Collector<T, Set<T>> =
    object : CollectorI<MutableSet<T>, T, Set<T>> {
      override val characteristics: Set<Characteristics> =
        setOf(Characteristics.IDENTITY_FINISH, Characteristics.UNORDERED)

      override suspend fun supply(): MutableSet<T> = mutableSetOf()
      override suspend fun accumulate(current: MutableSet<T>, value: T) {
        current.add(value)
      }

      override suspend fun finish(current: MutableSet<T>): Set<T> = current
    }

  /**
   * Collects all the values in a map.
   */
  public fun <K, V> mapFromEntries(): Collector<Map.Entry<K, V>, Map<K, V>> =
    map<K, V>().contramap { (k, v) -> k to v }

  /**
   * Collects all the values in a map.
   */
  public fun <K, V> map(): Collector<Pair<K, V>, Map<K, V>> =
    object : CollectorI<MutableMap<K, V>, Pair<K, V>, Map<K, V>> {
      override val characteristics: Set<Characteristics> =
        setOf(Characteristics.IDENTITY_FINISH, Characteristics.UNORDERED)

      override suspend fun supply(): MutableMap<K, V> = mutableMapOf()
      override suspend fun accumulate(current: MutableMap<K, V>, value: Pair<K, V>) {
        current[value.first] = value.second
      }

      override suspend fun finish(current: MutableMap<K, V>): Map<K, V> = current
    }
}
