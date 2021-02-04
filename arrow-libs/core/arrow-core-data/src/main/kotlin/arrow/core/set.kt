package arrow.core

import arrow.typeclasses.Hash
import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup
import arrow.typeclasses.hashWithSalt

object SetExtensions

object SortedSetInstances

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

fun <A> Hash.Companion.set(HA: Hash<A>): Hash<Set<A>> = object : Hash<Set<A>> {
  override fun Set<A>.hashWithSalt(salt: Int): Int =
    hashWithSalt(HA, salt)
}
