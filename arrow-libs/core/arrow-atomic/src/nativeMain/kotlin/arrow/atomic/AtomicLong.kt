package arrow.atomic

import kotlin.concurrent.AtomicLong

public actual class AtomicLong actual constructor(initialValue: Long) {
  private val inner = AtomicLong(initialValue)

  public actual fun get(): Long = inner.value

  public actual fun set(newValue: Long) {
    inner.value = newValue
  }

  public actual fun incrementAndGet(): Long =
    inner.incrementAndGet()

  public actual fun decrementAndGet(): Long =
    inner.decrementAndGet()

  public actual fun addAndGet(delta: Long): Long =
    inner.addAndGet(delta)

  public actual fun compareAndSet(expected: Long, new: Long): Boolean =
    inner.compareAndSet(expected, new)

  public actual fun getAndSet(value: Long): Long =
    inner.getAndSet(value)
}

