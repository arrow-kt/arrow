@file:OptIn(ExperimentalAtomicApi::class)

package arrow.atomic

import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.decrementAndFetch
import kotlin.concurrent.atomics.incrementAndFetch
import kotlin.concurrent.atomics.AtomicInt as KtAtomicInt

public actual class AtomicInt actual constructor(initialValue: Int) {
  private val inner = KtAtomicInt(initialValue)

  public actual fun get(): Int = inner.load()
  public actual fun set(newValue: Int) { inner.store(newValue) }
  public actual fun getAndSet(value: Int): Int = inner.exchange(value)

  public actual fun incrementAndGet(): Int = inner.incrementAndFetch()
  public actual fun decrementAndGet(): Int = inner.decrementAndFetch()

  public actual fun addAndGet(delta: Int): Int = inner.addAndFetch(delta)

  public actual fun compareAndSet(expected: Int, new: Int): Boolean = inner.compareAndSet(expected, new)
}
