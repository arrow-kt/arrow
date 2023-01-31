package arrow.atomic

public actual class AtomicInt actual constructor(initialValue: Int) {
  private var internalValue: Int = initialValue

  public actual fun get(): Int = internalValue

  public actual fun set(newValue: Int) {
    internalValue = newValue
  }

  public actual fun addAndGet(delta: Int): Int {
    internalValue += delta
    return internalValue
  }

  public actual fun incrementAndGet(): Int = addAndGet(1)

  public actual fun decrementAndGet(): Int = addAndGet(-1)

  public actual fun compareAndSet(expected: Int, new: Int): Boolean =
    if (expected == internalValue) {
      internalValue = new
      true
    } else {
      false
    }

  public actual fun getAndSet(value: Int): Int {
    val current = internalValue
    internalValue = value
    return current
  }
}
