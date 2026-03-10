@file:OptIn(ExperimentalAtomicApi::class)

package arrow.atomic

import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.AtomicReference as KtAtomicReference

public actual class Atomic<V> actual constructor(initialValue: V) {
  private val inner = KtAtomicReference(initialValue)

  public actual fun get(): V = inner.load()
  public actual fun set(value: V) { inner.store(value) }
  public actual fun getAndSet(value: V): V = inner.exchange(value)

  /**
   * Compare current value with expected and set to new if they're the same. Note, 'compare' is checking
   * the actual object id, not 'equals'.
   */
  public actual fun compareAndSet(expected: V, new: V): Boolean = inner.compareAndSet(expected, new)
}
