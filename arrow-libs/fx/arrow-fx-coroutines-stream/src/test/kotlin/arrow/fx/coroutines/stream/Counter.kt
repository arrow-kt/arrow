package arrow.fx.coroutines.stream

import arrow.fx.coroutines.Atomic

class Counter private constructor(private val atomic: Atomic<Int>) {

  suspend fun increment(): Unit =
    atomic.update(Int::inc)

  suspend fun decrement(): Unit =
    atomic.update(Int::dec)

  suspend fun count(): Int =
    atomic.get()

  companion object {
    suspend operator fun invoke(): Counter =
      Counter(Atomic(0))
  }
}
