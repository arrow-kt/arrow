@file:OptIn(FreezingIsDeprecated::class)
package arrow.atomic

import kotlin.native.concurrent.AtomicReference
import kotlin.native.concurrent.freeze
import kotlin.native.concurrent.isFrozen

public actual fun <A> Atomic(initialValue: A): Atomic<A> =
  AtomicRef(AtomicReference(initialValue.freeze()))

private class AtomicRef<V>(private val atom: AtomicReference<V>): Atomic<V> {

  override fun getAndSet(value: V): V {
    if (atom.isFrozen) value.freeze()
    while (true) {
      val cur = atom.value
      if (cur === value) return cur
      if (atom.compareAndSwap(cur, value) === cur) return cur
    }
  }
  
  override fun compareAndSet(expected: V, new: V): Boolean =
    atom.compareAndSet(expected, new.freeze())
  
  override var value: V
    get() = atom.value
    set(value) {
      atom.value = value.freeze()
    }
  
  override fun setAndGet(value: V): V {
    this.value = value
    return value
  }
}
