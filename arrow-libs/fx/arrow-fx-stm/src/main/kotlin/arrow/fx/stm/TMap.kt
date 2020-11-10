package arrow.fx.stm

import arrow.fx.stm.internal.Hamt
import arrow.fx.stm.internal.newHamt
import arrow.typeclasses.Hash

fun <K, V> STM.newTMap(fn: (K) -> Int): TMap<K, V> = TMap(newHamt(), fn)
fun <K, V> STM.newTMap(): TMap<K, V> = newTMap { it.hashCode() }
fun <K, V> STM.newTMap(hash: Hash<K>): TMap<K, V> = newTMap { hash.run { it.hash() } }

data class TMap<K, V>internal constructor(internal val hamt: Hamt<Pair<K, V>>, internal val hashFn: (K) -> Int) {
  companion object {
    suspend fun <K, V> new(fn: (K) -> Int): TMap<K, V> = TMap(Hamt.new(), fn)
    suspend fun <K, V> new(): TMap<K, V> = new { it.hashCode() }
    suspend fun <K, V> new(hash: Hash<K>): TMap<K, V> = new { hash.run { it.hash() } }
  }
}
