package arrow.atomic

import kotlin.concurrent.AtomicInt

public actual class AtomicInt actual constructor(initialValue: Int) {
  private val inner = AtomicInt(initialValue)

  public actual fun get(): Int = inner.value

  public actual fun set(newValue: Int) {
    inner.value = newValue
  }

  public actual fun incrementAndGet(): Int =
    inner.incrementAndGet()

  public actual fun decrementAndGet(): Int =
    inner.decrementAndGet()

  public actual fun addAndGet(delta: Int): Int =
    inner.addAndGet(delta)

  public actual fun compareAndSet(expected: Int, new: Int): Boolean =
    inner.compareAndSet(expected, new)

  public actual fun getAndSet(value: Int): Int =
    inner.getAndSet(value)
}
