package arrow.atomic

public actual fun <A> Atomic(initialValue: A): Atomic<A> =
  AtomicRef(initialValue)

private class AtomicRef<V>(private var internalValue: V) : Atomic<V> {
  
  /**
   * Compare current value with expected and set to new if they're the same. Note, 'compare' is checking
   * the actual object id, not 'equals'.
   */
  override fun compareAndSet(expected: V, new: V): Boolean {
    return if (expected === internalValue) {
      internalValue = new
      true
    } else {
      false
    }
  }
  
  override fun getAndSet(value: V): V {
    val oldValue = internalValue
    internalValue = value
    return oldValue
  }
  
  override fun setAndGet(value: V): V {
    this.internalValue = value
    return value
  }
  
  
  override var value: V
    get() = internalValue
    set(value) {
      internalValue = value
    }
}
