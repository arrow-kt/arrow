package arrow.atomic

public class AtomicBoolean(value: Boolean) {
  private val inner = AtomicInt(value.toInt())

  public var value: Boolean
    get() = inner.value != 0
    set(value) {
      inner.value = value.toInt()
    }

  public fun compareAndSet(expected: Boolean, new: Boolean): Boolean =
    inner.compareAndSet(expected.toInt(), new.toInt())

  public fun get(): Boolean = value
  public fun set(value: Boolean) {
     this.value = value
  }

  public fun getAndSet(value: Boolean): Boolean =
    inner.getAndSet(value.toInt()) == 1

  private fun Boolean.toInt(): Int =
    if (this) 1 else 0
}


/**
 * Infinite loop that reads this atomic variable and performs the specified [action] on its value.
 */
public inline fun AtomicBoolean.loop(action: (Boolean) -> Unit): Nothing = forever { action(value) }

public inline fun AtomicBoolean.tryUpdate(function: (Boolean) -> Boolean): Boolean = tryUpdate(function) { _, _ -> }

public inline fun AtomicBoolean.update(function: (Boolean) -> Boolean): Unit = update(function) { _, _ -> }

/**
 * Updates variable atomically using the specified [function] of its value and returns its old value.
 */
public inline fun AtomicBoolean.getAndUpdate(function: (Boolean) -> Boolean): Boolean = update(function) { old, _ -> old }

/**
 * Updates variable atomically using the specified [function] of its value and returns its new value.
 */
public inline fun AtomicBoolean.updateAndGet(function: (Boolean) -> Boolean): Boolean = update(function) { _, new -> new }

@PublishedApi
internal inline fun <R> AtomicBoolean.update(function: (Boolean) -> Boolean, transform: (old: Boolean, new: Boolean) -> R): R = forever {
  tryUpdate(function) { old, new -> return transform(old, new) }
}

@PublishedApi
internal inline fun AtomicBoolean.tryUpdate(function: (Boolean) -> Boolean, onUpdated: (old: Boolean, new: Boolean) -> Unit): Boolean {
  val cur = value
  val upd = function(cur)
  return compareAndSet(cur, upd).also { if (it) onUpdated(cur, upd) }
}
