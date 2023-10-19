package arrow.atomic

import kotlin.concurrent.AtomicReference

public actual class Atomic<V> actual constructor(initialValue: V) {
  private val inner = AtomicReference(initialValue)

  public actual fun get(): V = inner.value

  public actual fun set(value: V) {
    inner.value = value
  }

  public actual fun compareAndSet(expected: V, new: V): Boolean =
    inner.compareAndSet(expected, new)

  public actual fun getAndSet(value: V): V =
    inner.getAndSet(value)
}
