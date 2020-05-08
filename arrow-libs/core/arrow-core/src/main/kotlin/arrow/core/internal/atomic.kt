package arrow.core.internal

import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.updateAndGet
import kotlinx.atomicfu.getAndUpdate

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

  fun getAndSet(a: A): A =
    atomicRef.getAndSet(a)

  fun updateAndGet(function: (A) -> A): A =
    atomicRef.updateAndGet(function)

  fun getAndUpdate(f: (A) -> A): A =
    atomicRef.getAndUpdate(f)

  fun compareAndSet(expect: A, update: A): Boolean =
    atomicRef.compareAndSet(expect, update)

  fun lazySet(a: A): Unit =
    atomicRef.lazySet(a)

  override fun toString(): String =
    value.toString()
}

class AtomicBooleanW(a: Boolean) {
  private val atomicRef = atomic(a)

  var value: Boolean
    set(a) {
      atomicRef.value = a
    }
    get() = atomicRef.value

  fun getAndSet(a: Boolean): Boolean =
    atomicRef.getAndSet(a)

  fun updateAndGet(function: (Boolean) -> Boolean): Boolean =
    atomicRef.updateAndGet(function)

  fun getAndUpdate(f: (Boolean) -> Boolean): Boolean =
    atomicRef.getAndUpdate(f)

  fun compareAndSet(expect: Boolean, update: Boolean): Boolean =
    atomicRef.compareAndSet(expect, update)

  fun lazySet(a: Boolean): Unit =
    atomicRef.lazySet(a)

  override fun toString(): String =
    value.toString()
}

class AtomicIntW(a: Int) {
  private val atomicRef = atomic(a)

  var value: Int
    set(a) {
      atomicRef.value = a
    }
    get() = atomicRef.value

  fun getAndSet(a: Int): Int =
    atomicRef.getAndSet(a)

  fun getAndAdd(delta: Int): Int =
    atomicRef.getAndAdd(delta)

  fun addAndGet(delta: Int): Int =
    atomicRef.addAndGet(delta)

  fun getAndIncrement(): Int =
    atomicRef.getAndIncrement()

  fun getAndDecrement(): Int =
    atomicRef.getAndDecrement()

  fun incrementAndGet(): Int =
    atomicRef.incrementAndGet()

  fun decrementAndGet(): Int =
    atomicRef.decrementAndGet()

  fun updateAndGet(function: (Int) -> Int): Int =
    atomicRef.updateAndGet(function)

  fun getAndUpdate(f: (Int) -> Int): Int =
    atomicRef.getAndUpdate(f)

  fun compareAndSet(expect: Int, update: Int): Boolean =
    atomicRef.compareAndSet(expect, update)

  fun lazySet(a: Int): Unit =
    atomicRef.lazySet(a)

  override fun toString(): String =
    value.toString()
}
