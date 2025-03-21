@file:OptIn(ExperimentalAtomicApi::class)

package arrow.core

import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.jvm.JvmInline
import kotlin.jvm.JvmOverloads

public interface MemoizationCache<K, V> {
  public fun get(key: K): V?
  public fun set(key: K, value: V): V
}

@JvmInline
public value class AtomicMemoizationCache<K, V>(
  private val cache: AtomicReference<Map<K, V>> = AtomicReference(emptyMap())
): MemoizationCache<K, V> {
  override fun get(key: K): V? = cache.load()[key]
  override fun set(key: K, value: V): V = cache.updateAndGet { old ->
    if (key in old) old else old + (key to value)
  }.getValue(key)
}

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
@JvmOverloads
public fun <T, R> MemoizedDeepRecursiveFunction(
  cache: MemoizationCache<T, R> = AtomicMemoizationCache(),
  block: suspend DeepRecursiveScope<T, R>.(T) -> R
): DeepRecursiveFunction<T, R> = DeepRecursiveFunction { x ->
  when (val v = cache.get(x)) {
    null -> cache.set(x, block(x))
    else -> v
  }
}
