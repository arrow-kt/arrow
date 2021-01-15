package arrow.core

import arrow.typeclasses.Eq
import arrow.typeclasses.Hash
import arrow.typeclasses.hashWithSalt

object SetExtensions

object SortedSetInstances

fun <A> Set<A>.eqv(EQA: Eq<A>, b: Set<A>): Boolean =
  if (size == b.size) EQA.run {
    fold(true) { acc, aa ->
      val found = (b.find { bb -> aa.eqv(bb) } != null)
      acc && found
    }
  } else false

fun <A> Set<A>.hashWithSalt(HA: Hash<A>, salt: Int): Int = HA.run {
  fold(salt) { hash, v -> v.hashWithSalt(hash) }
}.hashWithSalt(size)
