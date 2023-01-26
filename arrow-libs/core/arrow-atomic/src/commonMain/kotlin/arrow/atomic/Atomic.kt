package arrow.atomic

/**
 * [Atomic] value of [A].
 *
 * ```kotlin
 * import arrow.atomic.AtomicInt
 * import arrow.atomic.update
 * import arrow.atomic.value
 * import arrow.fx.coroutines.parMap
 *
 * suspend fun main() {
 *   val count = AtomicInt(0)
 *   (0 until 20_000).parMap {
 *     count.update(Int::inc)
 *   }
 *   println(count.value)
 * }
 * ```
 * <!--- KNIT example-atomic-01.kt -->
 *
 * [Atomic] also offers some other interesting operators such as [loop], [update], [tryUpdate], etc.
 *
 * **WARNING**: Use [AtomicInt] and [AtomicLong] for [Int] and [Long] on Kotlin Native!
 */
public expect class Atomic<V>(initialValue: V) {
  public fun get(): V
  public fun set(value: V)
  public fun getAndSet(value: V): V

  /**
   * Compare current value with expected and set to new if they're the same. Note, 'compare' is checking
   * the actual object id, not 'equals'.
   */
  public fun compareAndSet(expected: V, new: V): Boolean
}

public var <T> Atomic<T>.value: T
  get() = get()
  set(value) {
    set(value)
  }

/**
 * Infinite loop that reads this atomic variable and performs the specified [action] on its value.
 */
public inline fun <V> Atomic<V>.loop(action: (V) -> Unit): Nothing {
  while (true) {
    action(value)
  }
}

public fun <V> Atomic<V>.tryUpdate(function: (V) -> V): Boolean {
  val cur = value
  val upd = function(cur)
  return compareAndSet(cur, upd)
}

public inline fun <V> Atomic<V>.update(function: (V) -> V) {
  while (true) {
    val cur = value
    val upd = function(cur)
    if (compareAndSet(cur, upd)) return
  }
}

/**
 * Updates variable atomically using the specified [function] of its value and returns its old value.
 */
public inline fun <V> Atomic<V>.getAndUpdate(function: (V) -> V): V {
  while (true) {
    val cur = value
    val upd = function(cur)
    if (compareAndSet(cur, upd)) return cur
  }
}

/**
 * Updates variable atomically using the specified [function] of its value and returns its new value.
 */
public inline fun <V> Atomic<V>.updateAndGet(function: (V) -> V): V {
  while (true) {
    val cur = value
    val upd = function(cur)
    if (compareAndSet(cur, upd)) return upd
  }
}
