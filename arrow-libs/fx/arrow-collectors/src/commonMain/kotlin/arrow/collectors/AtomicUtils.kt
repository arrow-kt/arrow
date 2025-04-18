@file:OptIn(ExperimentalAtomicApi::class)

package arrow.collectors

import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi

internal inline fun AtomicInt.update(block: (Int) -> Int) {
  while (true) {
    val old = load()
    val new = block(old)
    if (compareAndSet(old, new)) return
  }
}

internal inline fun <T> AtomicReference<T>.update(block: (T) -> T) {
  while (true) {
    val old = load()
    val new = block(old)
    if (compareAndSet(old, new)) return
  }
}
