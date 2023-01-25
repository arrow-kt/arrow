package arrow.atomic

public actual class Atomic<V> actual constructor(initialValue: V) {
  private var internalValue: V = initialValue

  public actual fun get(): V = internalValue

  public actual fun set(value: V) {
    internalValue = value
  }

  public actual fun compareAndSet(expected: V, new: V): Boolean =
    if (expected === internalValue) {
      internalValue = new
      true
    } else {
      false
    }

  public actual fun getAndSet(value: V): V {
    val old = internalValue
    internalValue = value
    return old
  }
}
