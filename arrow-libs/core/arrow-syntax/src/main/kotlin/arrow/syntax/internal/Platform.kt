package arrow.syntax.internal

import java.util.concurrent.ConcurrentHashMap

object Platform {

  interface ConcurrentMap<K, V> : MutableMap<K, V> {
    fun putSafely(k: K, v: V): V
  }

  fun <K, V> newConcurrentMap(): ConcurrentMap<K, V> {
    val map by lazy { ConcurrentHashMap<K, V>() }
    return object : ConcurrentMap<K, V>, MutableMap<K, V> by map {
      override fun putSafely(k: K, v: V): V =
        map.putIfAbsent(k, v) ?: v
    }
  }
}
