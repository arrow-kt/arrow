@file:OptIn(ExperimentalContracts::class)

package arrow.atomic

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public expect class AtomicLong(initialValue: Long) {

  public fun get(): Long
  public fun set(newValue: Long)
  public fun getAndSet(value: Long): Long

  public fun incrementAndGet(): Long
  public fun decrementAndGet(): Long

  public fun addAndGet(delta: Long): Long

  public fun compareAndSet(expected: Long, new: Long): Boolean
}

public var AtomicLong.value: Long
  get() = get()
  set(value) {
    set(value)
  }

/**
 * Infinite loop that reads this atomic variable and performs the specified [action] on its value.
 */
public inline fun AtomicLong.loop(action: (Long) -> Unit): Nothing {
  contract { callsInPlace(action, InvocationKind.AT_LEAST_ONCE) }
  do { action(value) } while(true)
}

public inline fun AtomicLong.tryUpdate(function: (Long) -> Long): Boolean {
  contract { callsInPlace(function, InvocationKind.EXACTLY_ONCE) }
  return tryUpdate(function) { _, _ -> }
}

public inline fun AtomicLong.update(function: (Long) -> Long) {
  contract { callsInPlace(function, InvocationKind.AT_LEAST_ONCE) }
  update(function) { _, _ -> }
}

/**
 * Updates variable atomically using the specified [function] of its value and returns its old value.
 */
public inline fun AtomicLong.getAndUpdate(function: (Long) -> Long): Long {
  contract { callsInPlace(function, InvocationKind.AT_LEAST_ONCE) }
  return update(function) { old, _ -> old }
}

/**
 * Updates variable atomically using the specified [function] of its value and returns its new value.
 */
public inline fun AtomicLong.updateAndGet(function: (Long) -> Long): Long {
  contract { callsInPlace(function, InvocationKind.AT_LEAST_ONCE) }
  return update(function) { _, new -> new }
}

@PublishedApi
internal inline fun <R> AtomicLong.update(function: (Long) -> Long, transform: (old: Long, new: Long) -> R): R {
  contract {
    callsInPlace(function, InvocationKind.AT_LEAST_ONCE)
    callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
  }
  loop { cur ->
    val upd = function(value)
    if(compareAndSet(cur, upd)) return transform(cur, upd)
  }
}

@PublishedApi
internal inline fun AtomicLong.tryUpdate(function: (Long) -> Long, onUpdated: (old: Long, new: Long) -> Unit): Boolean {
  contract {
    callsInPlace(function, InvocationKind.EXACTLY_ONCE)
    callsInPlace(onUpdated, InvocationKind.AT_MOST_ONCE)
  }
  val cur = value
  val upd = function(cur)
  return compareAndSet(cur, upd).also { if (it) onUpdated(cur, upd) }
}
