@file:OptIn(FreezingIsDeprecated::class)

package arrow.atomic

import kotlin.concurrent.AtomicReference
import kotlin.native.concurrent.freeze
import kotlin.native.concurrent.isFrozen

public actual class Atomic<V> actual constructor(initialValue: V) {
  private val inner = AtomicReference(initialValue.freeze())

  public actual fun get(): V = inner.value

  public actual fun set(value: V) {
    inner.value = value.freeze()
  }

  public actual fun compareAndSet(expected: V, new: V): Boolean =
    inner.compareAndSet(expected, new.freeze())

  public actual fun getAndSet(value: V): V {
    if (inner.isFrozen) value.freeze()
    while (true) {
      val cur = inner.value
      if (cur === value) return cur
      if (inner.compareAndSet(cur, value)) return cur
    }
  }
}
