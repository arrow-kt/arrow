package arrow.meta.internal

import java.util.concurrent.ConcurrentHashMap

fun <P1, R> ((P1) -> R).memoize(): (P1) -> R = object : (P1) -> R {
  private val m = MemoizedHandler<((P1) -> R), MemoizeKey1<P1, R>, R>(this@memoize)
  override fun invoke(p1: P1) = m(MemoizeKey1(p1))
}

private interface MemoizedCall<in F, out R> {
  operator fun invoke(f: F): R
}

private data class MemoizeKey1<out P1, R>(val p1: P1) : MemoizedCall<(P1) -> R, R> {
  override fun invoke(f: (P1) -> R) = f(p1)
}

private class MemoizedHandler<F, in K : MemoizedCall<F, R>, out R>(val f: F) {
  private val m = Platform.newConcurrentMap<K, R>()
  operator fun invoke(k: K): R = m[k] ?: run { m.putSafely(k, k(f)) }
}

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
