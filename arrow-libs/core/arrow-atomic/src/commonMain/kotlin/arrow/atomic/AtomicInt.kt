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
public inline fun AtomicInt.loop(action: (Int) -> Unit): Nothing {
  while (true) {
    action(value)
  }
}

public fun AtomicInt.tryUpdate(function: (Int) -> Int): Boolean {
  val cur = value
  val upd = function(cur)
  return compareAndSet(cur, upd)
}

public inline fun AtomicInt.update(function: (Int) -> Int) {
  while (true) {
    val cur = value
    val upd = function(cur)
    if (compareAndSet(cur, upd)) return
  }
}

/**
 * Updates variable atomically using the specified [function] of its value and returns its old value.
 */
public inline fun AtomicInt.getAndUpdate(function: (Int) -> Int): Int {
  while (true) {
    val cur = value
    val upd = function(cur)
    if (compareAndSet(cur, upd)) return cur
  }
}

/**
 * Updates variable atomically using the specified [function] of its value and returns its new value.
 */
public inline fun AtomicInt.updateAndGet(function: (Int) -> Int): Int {
  while (true) {
    val cur = value
    val upd = function(cur)
    if (compareAndSet(cur, upd)) return upd
  }
}

