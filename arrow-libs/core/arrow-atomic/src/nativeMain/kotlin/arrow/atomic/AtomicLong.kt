package arrow.atomic

import kotlin.concurrent.AtomicLong
import kotlin.native.concurrent.freeze
import kotlin.native.concurrent.isFrozen

public actual class AtomicLong actual constructor(initialValue: Long) {
  private val inner = AtomicLong(initialValue)

  public actual fun get(): Long = inner.value

  public actual fun set(newValue: Long) {
    inner.value = newValue
  }

  public actual fun incrementAndGet(): Long =
    inner.addAndGet(1L)

  public actual fun decrementAndGet(): Long =
    inner.addAndGet(-1L)

  public actual fun addAndGet(delta: Long): Long =
    inner.addAndGet(delta)

  public actual fun compareAndSet(expected: Long, new: Long): Boolean =
    inner.compareAndSet(expected, new)

  public actual fun getAndSet(value: Long): Long {
    if (inner.isFrozen) value.freeze()
    while (true) {
      val cur = inner.value
      if (cur == value) return cur
      if (inner.compareAndSet(cur, value)) return cur
    }
  }
}

