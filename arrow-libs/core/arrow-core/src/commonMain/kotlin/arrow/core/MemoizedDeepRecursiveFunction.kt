package arrow.core

import arrow.atomic.Atomic
import arrow.atomic.loop

public fun <T, R> MemoizedDeepRecursiveFunction(
  block: suspend DeepRecursiveScope<T, R>.(T) -> R
): DeepRecursiveFunction<T, R> {
  val cache = Atomic(emptyMap<T, R>())
  return DeepRecursiveFunction { x ->
    when (x) {
      in cache.get() -> cache.get().getValue(x)
      else -> {
        val result = block(x)
        cache.loop { old ->
          when (x) {
            in old ->
              return@DeepRecursiveFunction old.getValue(x)
            else -> {
              if (cache.compareAndSet(old, old + Pair(x, result)))
                return@DeepRecursiveFunction result
            }
          }
        }
      }
    }
  }
}
