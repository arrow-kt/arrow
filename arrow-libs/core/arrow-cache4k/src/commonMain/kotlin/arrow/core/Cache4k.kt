package arrow.core

import io.github.reactivecircus.cache4k.Cache
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmInline

@JvmInline
public value class Cache4kMemoizationCache<K: Any, V: Any>(
  private val cache: Cache<K, V>
): MemoizationCache<K, V> {
  override fun get(key: K): V? = cache.get(key)
  override fun set(key: K, value: V): V = value.also { cache.put(key, value) }
}

@OptIn(ExperimentalTypeInference::class)
public fun <K: Any, V: Any> buildCache4K(
  @BuilderInference configure: Cache.Builder<K, V>.() -> Unit
): Cache<K, V> = Cache.Builder<K, V>().apply(configure).build()
