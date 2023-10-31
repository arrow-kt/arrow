package arrow.atomic

import kotlin.jvm.JvmInline

@JvmInline
public value class AtomicBoolean private constructor(private val inner: AtomicInt) {
  public constructor(value: Boolean): this(AtomicInt(value.toInt()))

  public var value: Boolean
    get() = inner.value.toBoolean()
    set(value) {
      inner.value = value.toInt()
    }

  public fun compareAndSet(expected: Boolean, new: Boolean): Boolean =
    inner.compareAndSet(expected.toInt(), new.toInt())

  public fun get(): Boolean = value
  public fun set(value: Boolean) {
    inner.value = value.toInt()
  }

  public fun getAndSet(value: Boolean): Boolean =
    inner.getAndSet(value.toInt()).toBoolean()
}

private inline fun Boolean.toInt(): Int =
  if (this) 1 else 0

private inline fun Int.toBoolean(): Boolean =
  this != 0


/**
 * Infinite loop that reads this atomic variable and performs the specified [action] on its value.
 */
public inline fun AtomicBoolean.loop(action: (Boolean) -> Unit): Nothing {
  while (true) {
    action(value)
  }
}

public fun AtomicBoolean.tryUpdate(function: (Boolean) -> Boolean): Boolean {
  val cur = value
  val upd = function(cur)
  return compareAndSet(cur, upd)
}

public inline fun AtomicBoolean.update(function: (Boolean) -> Boolean) {
  while (true) {
    val cur = value
    val upd = function(cur)
    if (compareAndSet(cur, upd)) return
  }
}

/**
 * Updates variable atomically using the specified [function] of its value and returns its old value.
 */
public inline fun AtomicBoolean.getAndUpdate(function: (Boolean) -> Boolean): Boolean {
  while (true) {
    val cur = value
    val upd = function(cur)
    if (compareAndSet(cur, upd)) return cur
  }
}

/**
 * Updates variable atomically using the specified [function] of its value and returns its new value.
 */
public inline fun AtomicBoolean.updateAndGet(function: (Boolean) -> Boolean): Boolean {
  while (true) {
    val cur = value
    val upd = function(cur)
    if (compareAndSet(cur, upd)) return upd
  }
}

