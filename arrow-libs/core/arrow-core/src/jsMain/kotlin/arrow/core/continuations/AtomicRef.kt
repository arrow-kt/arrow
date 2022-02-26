package arrow.core.continuations

public actual class AtomicRef<V> actual constructor(initialValue: V) {
  private var internalValue: V = initialValue

  /**
   * Compare current value with expected and set to new if they're the same. Note, 'compare' is checking
   * the actual object id, not 'equals'.
   */
  public actual fun compareAndSet(expected: V, new: V): Boolean {
    return if (expected === internalValue) {
      internalValue = new
      true
    } else {
      false
    }
  }

  public actual fun getAndSet(value: V): V {
    val oldValue = internalValue
    internalValue = value
    return oldValue
  }

  public actual fun get(): V = internalValue

  public actual fun set(value: V) {
    internalValue = value
  }
}
