package arrow.atomic

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
  while (true) {
    action(value)
  }
}

public fun AtomicLong.tryUpdate(function: (Long) -> Long): Boolean {
  val cur = value
  val upd = function(cur)
  return compareAndSet(cur, upd)
}

public inline fun AtomicLong.update(function: (Long) -> Long) {
  while (true) {
    val cur = value
    val upd = function(cur)
    if (compareAndSet(cur, upd)) return
  }
}

/**
 * Updates variable atomically using the specified [function] of its value and returns its old value.
 */
public inline fun AtomicLong.getAndUpdate(function: (Long) -> Long): Long {
  while (true) {
    val cur = value
    val upd = function(cur)
    if (compareAndSet(cur, upd)) return cur
  }
}

/**
 * Updates variable atomically using the specified [function] of its value and returns its new value.
 */
public inline fun AtomicLong.updateAndGet(function: (Long) -> Long): Long {
  while (true) {
    val cur = value
    val upd = function(cur)
    if (compareAndSet(cur, upd)) return upd
  }
}
