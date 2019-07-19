package arrow.persistent.data

import arrow.core.Eval
import arrow.core.MapK
import arrow.core.Option
import arrow.core.iterateRight
import arrow.persistent.internal.HashArrayMappedTrie

class ForPersistentMapK private constructor() {
  companion object
}
typealias PersistentMapKOf<K, A> = arrow.Kind2<ForPersistentMapK, K, A>
typealias PersistentMapKPartialOf<K> = arrow.Kind<ForPersistentMapK, K>
typealias PersistentMapKKindedJ<K, A> = io.kindedj.HkJ2<ForPersistentMapK, K, A>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <K, A> PersistentMapKOf<K, A>.fix(): PersistentMapK<K, A> =
  this as PersistentMapK<K, A>

/**
 * A [PersistentMapK] is an immutable [MapK] which implements structural sharing
 * by wrapping a persistent Map implementation.
 */
data class PersistentMapK<K, A>(
  private val map: HashArrayMappedTrie<K, A> = HashArrayMappedTrie.empty()) :
  PersistentMapKOf<K, A> {

  val isEmpty: Boolean = map.isEmpty
  val size: Int = map.size

  fun <B> map(f: (A) -> B): PersistentMapK<K, B> {
    var result = HashArrayMappedTrie.empty<K, B>()
    map.iterator().forEach { (a, b) ->
      result = result.put(a, f(b))
    }
    return PersistentMapK(result)
  }

  fun <B> foldLeft(b: B, f: (B, A) -> B): B {
    var result = b
    map.iterator().forEach { (_, value) ->
      result = f(b, value)
    }
    return result
  }

  fun <B> foldRight(b: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> {
    return this.map.valuesIterator().iterateRight(b, f)
  }

  fun combine(b: PersistentMapK<K, A>): PersistentMapK<K, A> {
    var result = this
    b.map.forEach { (key, value) ->
      result = result.put(key, value)
    }
    return result
  }

  fun getOption(key: K): Option<A> = map[key]

  fun containsKey(key: K): Boolean = map.containsKey(key)

  fun put(key: K, value: A): PersistentMapK<K, A> = PersistentMapK(map.put(key, value))

  fun remove(key: K): PersistentMapK<K, A> = PersistentMapK(map.remove(key))

  companion object
}
