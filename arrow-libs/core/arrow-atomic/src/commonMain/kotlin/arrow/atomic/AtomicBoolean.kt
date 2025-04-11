@file:OptIn(ExperimentalContracts::class, ExperimentalAtomicApi::class)

package arrow.atomic

import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.AtomicBoolean as KtAtomicBoolean
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public class AtomicBoolean(value: Boolean) {
  private val inner = KtAtomicBoolean(value)

  public var value: Boolean
    get() = inner.load()
    set(value) {
      inner.store(value)
    }

  public fun compareAndSet(expected: Boolean, new: Boolean): Boolean =
    inner.compareAndSet(expected, new)

  public fun get(): Boolean = value
  public fun set(value: Boolean) {
     this.value = value
  }

  public fun getAndSet(value: Boolean): Boolean =
    inner.exchange(value)
}


/**
 * Infinite loop that reads this atomic variable and performs the specified [action] on its value.
 */
public inline fun AtomicBoolean.loop(action: (Boolean) -> Unit): Nothing {
  contract { callsInPlace(action, InvocationKind.AT_LEAST_ONCE) }
  do { action(value) } while(true)
}

public inline fun AtomicBoolean.tryUpdate(function: (Boolean) -> Boolean): Boolean {
  contract { callsInPlace(function, InvocationKind.EXACTLY_ONCE) }
  return tryUpdate(function) { _, _ -> }
}

public inline fun AtomicBoolean.update(function: (Boolean) -> Boolean) {
  contract { callsInPlace(function, InvocationKind.AT_LEAST_ONCE) }
  update(function) { _, _ -> }
}

/**
 * Updates variable atomically using the specified [function] of its value and returns its old value.
 */
public inline fun AtomicBoolean.getAndUpdate(function: (Boolean) -> Boolean): Boolean {
  contract { callsInPlace(function, InvocationKind.AT_LEAST_ONCE) }
  return update(function) { old, _ -> old }
}

/**
 * Updates variable atomically using the specified [function] of its value and returns its new value.
 */
public inline fun AtomicBoolean.updateAndGet(function: (Boolean) -> Boolean): Boolean {
  contract { callsInPlace(function, InvocationKind.AT_LEAST_ONCE) }
  return update(function) { _, new -> new }
}

@PublishedApi
internal inline fun <R> AtomicBoolean.update(function: (Boolean) -> Boolean, transform: (old: Boolean, new: Boolean) -> R): R {
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
internal inline fun AtomicBoolean.tryUpdate(function: (Boolean) -> Boolean, onUpdated: (old: Boolean, new: Boolean) -> Unit): Boolean {
  contract {
    callsInPlace(function, InvocationKind.EXACTLY_ONCE)
    callsInPlace(onUpdated, InvocationKind.AT_MOST_ONCE)
  }
  val cur = value
  val upd = function(cur)
  return compareAndSet(cur, upd).also { if (it) onUpdated(cur, upd) }
}
