package arrow.atomic

public expect class AtomicInt(initialValue: Int) {

  public fun get(): Int
  public fun set(newValue: Int)
  public fun getAndSet(value: Int): Int

  public fun incrementAndGet(): Int
  public fun decrementAndGet(): Int

  public fun addAndGet(delta: Int): Int

  public fun compareAndSet(expected: Int, new: Int): Boolean
}

public var AtomicInt.value: Int
  get() = get()
  set(value) {
    set(value)
  }

/**
 * Infinite loop that reads this atomic variable and performs the specified [action] on its value.
 */
public inline fun AtomicInt.loop(action: (Int) -> Unit): Nothing = forever { action(value) }

public inline fun AtomicInt.tryUpdate(function: (Int) -> Int): Boolean = tryUpdate(function) { _, _ -> }

public inline fun AtomicInt.update(function: (Int) -> Int) : Unit = update(function) { _, _ -> }

/**
 * Updates variable atomically using the specified [function] of its value and returns its old value.
 */
public inline fun AtomicInt.getAndUpdate(function: (Int) -> Int): Int = update(function) { old, _ -> old }

/**
 * Updates variable atomically using the specified [function] of its value and returns its new value.
 */
public inline fun AtomicInt.updateAndGet(function: (Int) -> Int): Int = update(function) { _, new -> new }

@PublishedApi
internal inline fun <R> AtomicInt.update(function: (Int) -> Int, transform: (old: Int, new: Int) -> R): R = forever {
  tryUpdate(function) { old, new -> return transform(old, new) }
}

@PublishedApi
internal inline fun AtomicInt.tryUpdate(function: (Int) -> Int, onUpdated: (old: Int, new: Int) -> Unit): Boolean {
  val cur = value
  val upd = function(cur)
  return compareAndSet(cur, upd).also { if (it) onUpdated(cur, upd) }
}
