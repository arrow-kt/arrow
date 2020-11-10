package arrow.fx.stm

import arrow.fx.stm.internal.Hamt
import arrow.fx.stm.internal.newHamt
import arrow.typeclasses.Hash

fun <A> STM.newTSet(fn: (A) -> Int): TSet<A> = TSet(newHamt(), fn)
fun <A> STM.newTSet(): TSet<A> = newTSet { it.hashCode() }
fun <A> STM.newTSet(hash: Hash<A>): TSet<A> = newTSet { hash.run { it.hash() } }

data class TSet<A>internal constructor(internal val hamt: Hamt<Pair<Unit, A>>, internal val hashFn: (A) -> Int) {
  companion object {
    suspend fun <A> new(fn: (A) -> Int): TSet<A> = TSet(Hamt.new(), fn)
    suspend fun <A> new(): TSet<A> = new { it.hashCode() }
    suspend fun <A> new(hash: Hash<A>): TSet<A> = new { hash.run { it.hash() } }
  }
}
