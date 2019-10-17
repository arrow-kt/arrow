package arrow.core.extensions

import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.updateAndGet

// TODO should this be hidden from the outside?
class Atomic<A>(a: A) {
  private val atomicRef = atomic(a)

  var value: A
    set(a) {
      atomicRef.value = a
    }
    get() = atomicRef.value

  fun getAndSet(a: A) = atomicRef.getAndSet(a)

  fun updateAndGet(function: (A) -> A) = atomicRef.updateAndGet(function)

  fun compareAndSet(expect: A, update: A) = atomicRef.compareAndSet(expect, update)

  fun lazySet(a: A) = atomicRef.lazySet(a)
}
