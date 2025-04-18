@file:OptIn(ExperimentalAtomicApi::class)

package arrow.fx.coroutines

import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi

internal inline fun <T> AtomicReference<T>.update(block: (T) -> T) {
  while (true) {
    val old = load()
    val new = block(old)
    if (compareAndSet(old, new)) return
  }
}
