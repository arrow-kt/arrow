@file:OptIn(ExperimentalAtomicApi::class)

package arrow.core

import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi

internal inline fun <T> AtomicReference<T>.updateAndGet(block: (T) -> T): T {
  while (true) {
    val old = load()
    val new = block(old)
    if (compareAndSet(old, new)) return new
  }
}
