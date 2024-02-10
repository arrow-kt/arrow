package arrow.atomic

public actual class AtomicLong actual constructor(initialValue: Long) {
  private var internalValue: Long = initialValue

  public actual fun get(): Long = internalValue

  public actual fun set(newValue: Long) {
    internalValue = newValue
  }

  public actual fun addAndGet(delta: Long): Long {
    internalValue += delta
    return internalValue
  }

  public actual fun incrementAndGet(): Long = addAndGet(1)

  public actual fun decrementAndGet(): Long = addAndGet(-1)

  public actual fun compareAndSet(expected: Long, new: Long): Boolean =
    if (expected == internalValue) {
      internalValue = new
      true
    } else {
      false
    }

  public actual fun getAndSet(value: Long): Long {
    val current = internalValue
    internalValue = value
    return current
  }
}
