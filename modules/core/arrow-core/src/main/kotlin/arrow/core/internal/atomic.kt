package arrow.core.internal

import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.updateAndGet

/**
 * Internal wrapper for Atomic-FU Atomics to be used as local variables
 */
class AtomicRefW<A>(a: A) {
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

  override fun toString(): String = value.toString()
}

class AtomicBooleanW(a: Boolean) {
  private val atomicRef = atomic(a)

  var value: Boolean
    set(a) {
      atomicRef.value = a
    }
    get() = atomicRef.value

  fun getAndSet(a: Boolean) = atomicRef.getAndSet(a)

  fun updateAndGet(function: (Boolean) -> Boolean) = atomicRef.updateAndGet(function)

  fun compareAndSet(expect: Boolean, update: Boolean) = atomicRef.compareAndSet(expect, update)

  fun lazySet(a: Boolean) = atomicRef.lazySet(a)

  override fun toString(): String = value.toString()
}

class AtomicIntW(a: Int) {
  private val atomicRef = atomic(a)

  var value: Int
    set(a) {
      atomicRef.value = a
    }
    get() = atomicRef.value

  fun getAndSet(a: Int) = atomicRef.getAndSet(a)

  fun getAndAdd(delta: Int) = atomicRef.getAndAdd(delta)

  fun addAndGet(delta: Int) = atomicRef.addAndGet(delta)

  fun getAndIncrement() = atomicRef.getAndIncrement()

  fun getAndDecrement() = atomicRef.getAndDecrement()

  fun incrementAndGet() = atomicRef.incrementAndGet()

  fun decrementAndGet() = atomicRef.decrementAndGet()

  fun updateAndGet(function: (Int) -> Int) = atomicRef.updateAndGet(function)

  fun compareAndSet(expect: Int, update: Int) = atomicRef.compareAndSet(expect, update)

  fun lazySet(a: Int) = atomicRef.lazySet(a)

  override fun toString(): String = value.toString()
}
