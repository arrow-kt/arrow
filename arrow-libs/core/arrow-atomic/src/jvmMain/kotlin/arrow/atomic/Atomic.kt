@file:JvmName("AtomicActual")

package arrow.atomic

import java.util.concurrent.atomic.AtomicReference

public actual fun <A> Atomic(initialValue: A): Atomic<A> =
  AtomicRef(AtomicReference(initialValue))

private class AtomicRef<A> constructor(private val atom: AtomicReference<A>) : Atomic<A> {
  
  override var value: A
    get() = atom.get()
    set(value) {
      atom.set(value)
    }
  
  override fun compareAndSet(expected: A, new: A): Boolean = atom.compareAndSet(expected, new)
  
  override fun getAndSet(value: A): A = atom.getAndSet(value)
  
  override fun setAndGet(value: A): A {
    atom.set(value)
    return value
  }
}
