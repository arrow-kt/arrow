package arrow.core

import arrow.atomic.Atomic
import arrow.atomic.update
import arrow.atomic.value

/**
 * Defines a recursive **pure** function that:
 * - keeps its stack on the heap, which allows very deep recursive computations that do not use the actual call stack;
 * - memoizes every call, which means that the function is execute only once per argument.
 *
 * [MemoizedDeepRecursiveFunction] takes one parameter of type [T] and returns a result of type [R].
 * The [block] of code defines the body of a recursive function. In this block
 * [callRecursive][DeepRecursiveScope.callRecursive] function can be used to make a recursive call
 * to the declared function.
 */
public fun <T, R> MemoizedDeepRecursiveFunction(
  block: suspend DeepRecursiveScope<T, R>.(T) -> R
): DeepRecursiveFunction<T, R> {
  val cache = Atomic(emptyMap<T, R>())
  return DeepRecursiveFunction { x ->
    cache.value.onAvailable(x) { return@DeepRecursiveFunction it }

    val result = block(x)
    cache.update { old ->
      old.onAvailable(x) { return@DeepRecursiveFunction it }
      old + (x to result)
    }
    result
  }
}

private inline fun <K, V> Map<K, V>.onAvailable(key: K, action: (V) -> Unit) {
  if (key in this) action(getValue(key))
}
