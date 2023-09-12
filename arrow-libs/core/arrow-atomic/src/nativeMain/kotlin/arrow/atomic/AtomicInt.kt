package arrow.atomic

import kotlin.concurrent.AtomicInt
import kotlin.native.concurrent.freeze
import kotlin.native.concurrent.isFrozen

public actual class AtomicInt actual constructor(initialValue: Int) {
  private val inner = AtomicInt(initialValue)

  public actual fun get(): Int = inner.value

  public actual fun set(newValue: Int) {
    inner.value = newValue
  }

  public actual fun incrementAndGet(): Int =
    inner.addAndGet(1)

  public actual fun decrementAndGet(): Int =
    inner.addAndGet(-1)

  public actual fun addAndGet(delta: Int): Int =
    inner.addAndGet(delta)

  public actual fun compareAndSet(expected: Int, new: Int): Boolean =
    inner.compareAndSet(expected, new)

  public actual fun getAndSet(value: Int): Int {
    if (inner.isFrozen) value.freeze()
    while (true) {
      val cur = inner.value
      if (cur == value) return cur
      if (inner.compareAndSet(cur, value)) return cur
    }
  }
}
