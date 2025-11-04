@file:OptIn(ExperimentalContracts::class)

package arrow.atomic

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * [Atomic] value of [V].
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
  contract { callsInPlace(action, InvocationKind.AT_LEAST_ONCE) }
  do { action(value) } while(true)
}

public inline fun <V> Atomic<V>.tryUpdate(function: (V) -> V): Boolean {
  contract { callsInPlace(function, InvocationKind.EXACTLY_ONCE) }
  return tryUpdate(function) { _, _ -> }
}

public inline fun <V> Atomic<V>.update(function: (V) -> V) {
  contract { callsInPlace(function, InvocationKind.AT_LEAST_ONCE) }
  update(function) { _, _ -> }
}

/**
 * Updates variable atomically using the specified [function] of its value and returns its old value.
 */
public inline fun <V> Atomic<V>.getAndUpdate(function: (V) -> V): V {
  contract { callsInPlace(function, InvocationKind.AT_LEAST_ONCE) }
  return update(function) { old, _ -> old }
}

/**
 * Updates variable atomically using the specified [function] of its value and returns its new value.
 */
public inline fun <V> Atomic<V>.updateAndGet(function: (V) -> V): V {
  contract { callsInPlace(function, InvocationKind.AT_LEAST_ONCE) }
  return update(function) { _, new -> new }
}

public inline fun <V, U: V, R> Atomic<V>.update(function: (V) -> U, transform: (old: V, new: U) -> R): R {
  contract {
    callsInPlace(function, InvocationKind.AT_LEAST_ONCE)
    callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
  }
  loop { cur ->
    val upd = function(value)
    if(compareAndSet(cur, upd)) return transform(cur, upd)
  }
}

public inline fun <V, U: V> Atomic<V>.tryUpdate(function: (V) -> U, onUpdated: (old: V, new: U) -> Unit): Boolean {
  contract {
    callsInPlace(function, InvocationKind.EXACTLY_ONCE)
    callsInPlace(onUpdated, InvocationKind.AT_MOST_ONCE)
  }
  val cur = value
  val upd = function(cur)
  return compareAndSet(cur, upd).also { if (it) onUpdated(cur, upd) }
}
