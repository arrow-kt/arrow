package arrow.continuations.generic

@Deprecated("$deprecateArrowContinuation The AtomicRef APIs have been moved to arrow.core.generic", ReplaceWith("AtomicRef<V>", "arrow.core.generic.AtomicRef"))
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
@Deprecated("$deprecateArrowContinuation The AtomicRef APIs have been moved to arrow.core.generic", ReplaceWith("loop(action)", "arrow.core.generic.loop"))
public inline fun <V> AtomicRef<V>.loop(action: (V) -> Unit): Nothing {
  while (true) {
    action(get())
  }
}

@Deprecated("$deprecateArrowContinuation The AtomicRef APIs have been moved to arrow.core.generic", ReplaceWith("update(function)", "arrow.core.generic.update"))
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
@Deprecated("$deprecateArrowContinuation The AtomicRef APIs have been moved to arrow.core.generic", ReplaceWith("getAndUpdate(function)", "arrow.core.generic.getAndUpdate"))
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
@Deprecated("$deprecateArrowContinuation The AtomicRef APIs have been moved to arrow.core.generic", ReplaceWith("updateAndGet", "arrow.core.generic.updateAndGet"))
public inline fun <V> AtomicRef<V>.updateAndGet(function: (V) -> V): V {
  while (true) {
    val cur = get()
    val upd = function(cur)
    if (compareAndSet(cur, upd)) return upd
  }
}
