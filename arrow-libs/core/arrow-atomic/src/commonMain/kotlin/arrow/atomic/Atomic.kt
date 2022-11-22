package arrow.atomic

public expect fun <A> Atomic(initialValue: A): Atomic<A>

/**
 * [Atomic] value of [A].
 *
 * ```kotlin
 * import arrow.atomic.Atomic
 * import arrow.atomic.update
 * import arrow.fx.coroutines.parMap
 *
 * suspend fun main() {
 *   val count = Atomic(0)
 *   (0 until 20_000).parMap {
 *     count.update(Int::inc)
 *   }
 *   println(count.value)
 * }
 * ```
 * <!--- KNIT example-atomic-01.kt -->
 *
 * [Atomic] also offers some other interesting operators such as [loop], [update], [tryUpdate], etc.
 */
public interface Atomic<A> {
  public var value: A
  public fun getAndSet(value: A): A
  public fun setAndGet(value: A): A
  public fun compareAndSet(expected: A, new: A): Boolean
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
