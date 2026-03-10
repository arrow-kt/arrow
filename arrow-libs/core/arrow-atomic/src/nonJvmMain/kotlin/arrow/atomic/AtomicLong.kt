@file:OptIn(ExperimentalAtomicApi::class)

package arrow.atomic

import kotlin.concurrent.atomics.AtomicLong as KtAtomicLong
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.decrementAndFetch
import kotlin.concurrent.atomics.incrementAndFetch

public actual class AtomicLong actual constructor(initialValue: Long) {
  private val inner = KtAtomicLong(initialValue)

  public actual fun get(): Long = inner.load()
  public actual fun set(newValue: Long) { inner.store(newValue) }
  public actual fun getAndSet(value: Long): Long = inner.exchange(value)

  public actual fun incrementAndGet(): Long = inner.incrementAndFetch()
  public actual fun decrementAndGet(): Long = inner.decrementAndFetch()

  public actual fun addAndGet(delta: Long): Long = inner.addAndFetch(delta)

  public actual fun compareAndSet(expected: Long, new: Long): Boolean = inner.compareAndSet(expected, new)
}
