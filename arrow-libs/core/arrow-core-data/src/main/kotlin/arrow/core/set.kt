package arrow.core

import arrow.typeclasses.Eq
import arrow.typeclasses.Hash
import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup
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

fun <A> Semigroup.Companion.set(): Semigroup<Set<A>> = object : Semigroup<Set<A>> {
  override fun Set<A>.combine(b: Set<A>): Set<A> =
    this + b
}

fun <A> Monoid.Companion.set(): Monoid<Set<A>> = object : Monoid<Set<A>> {
  override fun empty(): Set<A> =
    emptySet<A>()

  override fun Set<A>.combine(b: Set<A>): Set<A> =
    this + b
}

fun <A> Eq.Companion.set(EQ: () -> Eq<A>): Eq<Set<A>> = object : Eq<Set<A>> {
  override fun Set<A>.eqv(b: Set<A>): Boolean =
    if (size == b.size) map { aa ->
      b.find { bb -> EQ().run { aa.eqv(bb) } } != null
    }.fold(true) { acc, bool ->
      acc && bool
    }
    else false
}

fun <A> Hash.Companion.set(HA: Hash<A>): Hash<Set<A>> = object : Hash<Set<A>> {
  override fun Set<A>.hashWithSalt(salt: Int): Int =
    hashWithSalt(HA, salt)
}
