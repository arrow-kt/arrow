package arrow.continuations.generic

public expect class AtomicRef<V>(initialValue: V) {
  public fun get(): V
  public fun set(value: V)
  public fun getAndSet(value: V): V

  /**
   * Compare current value with expected and set to new if they're the same. Note, 'compare' is checking
   * the actual object id, not 'equals'.
   */
  public fun compareAndSet(expected: V, new: V): Boolean
}

/**
 * Infinite loop that reads this atomic variable and performs the specified [action] on its value.
 */
public inline fun <V> AtomicRef<V>.loop(action: (V) -> Unit): Nothing {
  while (true) {
    action(get())
  }
}

public inline fun <V> AtomicRef<V>.update(function: (V) -> V) {
  while (true) {
    val cur = get()
    val upd = function(cur)
    if (compareAndSet(cur, upd)) return
  }
}

/**
 * Updates variable atomically using the specified [function] of its value and returns its old value.
 */
public inline fun <V> AtomicRef<V>.getAndUpdate(function: (V) -> V): V {
  while (true) {
    val cur = get()
    val upd = function(cur)
    if (compareAndSet(cur, upd)) return cur
  }
}

/**
 * Updates variable atomically using the specified [function] of its value and returns its new value.
 */
public inline fun <V> AtomicRef<V>.updateAndGet(function: (V) -> V): V {
  while (true) {
    val cur = get()
    val upd = function(cur)
    if (compareAndSet(cur, upd)) return upd
  }
}
