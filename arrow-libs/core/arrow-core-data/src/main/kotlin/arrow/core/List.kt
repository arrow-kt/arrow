package arrow.core

import arrow.typeclasses.Hash
import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup
import arrow.typeclasses.defaultSalt
import arrow.typeclasses.hashWithSalt
import kotlin.collections.plus as _plus

fun <A> Hash.Companion.list(HA: Hash<A>): Hash<List<A>> =
  ListHash(HA)

private class ListHash<A>(private val HA: Hash<A>) : Hash<List<A>> {
  override fun List<A>.hash(): Int = hash(HA)
  override fun List<A>.hashWithSalt(salt: Int): Int = hashWithSalt(HA, salt)
}

fun <A> List<A>.hash(HA: Hash<A>): Int =
  hashWithSalt(HA, defaultSalt)

fun <A> List<A>.hashWithSalt(HA: Hash<A>, salt: Int): Int = HA.run {
  fold(salt) { hash, x -> x.hashWithSalt(hash) }.hashWithSalt(size)
}

fun <A> Semigroup.Companion.list(): Semigroup<List<A>> =
  Monoid.list()

fun <A> Monoid.Companion.list(): Monoid<List<A>> =
  ListMonoid as Monoid<List<A>>

object ListMonoid : Monoid<List<Any?>> {
  override fun empty(): List<Any?> = emptyList()
  override fun List<Any?>.combine(b: List<Any?>): List<Any?> = this._plus(b)
}
