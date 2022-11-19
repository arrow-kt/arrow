package arrow.continuations.generic

import kotlin.native.concurrent.AtomicReference
import kotlin.native.concurrent.freeze
import kotlin.native.concurrent.isFrozen
import kotlin.native.FreezingIsDeprecated

@Deprecated(deprecateArrowContinuation)
@OptIn(FreezingIsDeprecated::class)
public actual class AtomicRef<V> actual constructor(initialValue: V) {
  private val atom = AtomicReference(initialValue.freeze())
  public actual fun get(): V = atom.value

  public actual fun set(value: V) {
    atom.value = value.freeze()
  }

  public actual fun getAndSet(value: V): V {
    if (atom.isFrozen) value.freeze()
    while (true) {
      val cur = atom.value
      if (cur === value) return cur
      if (atom.compareAndSwap(cur, value) === cur) return cur
    }
  }

  /**
   * Compare current value with expected and set to new if they're the same. Note, 'compare' is checking
   * the actual object id, not 'equals'.
   */
  public actual fun compareAndSet(expected: V, new: V): Boolean = atom.compareAndSet(expected, new.freeze())
}
