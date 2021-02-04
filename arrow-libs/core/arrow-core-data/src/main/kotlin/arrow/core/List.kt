package arrow.core

import arrow.typeclasses.Hash
import arrow.typeclasses.Monoid
import arrow.typeclasses.Order
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

fun <A> List<A>.compare(OA: Order<A>, b: List<A>): Ordering = OA.run {
  align(b) { ior -> ior.fold({ GT }, { LT }, { a1, a2 -> a1.compare(a2) }) }
    .fold(Monoid.ordering())
}

/**
 * Check if [this@lt] is `lower than` [b]
 *
 * @receiver object to compare with [b]
 * @param b object to compare with [this@lt]
 * @returns true if [this@lt] is `lower than` [b] and false otherwise
 */
fun <A> List<A>.lt(OA: Order<A>, b: List<A>): Boolean =
  compare(OA, b) == LT

/**
 * Check if [this@lte] is `lower than or equal to` [b]
 *
 * @receiver object to compare with [b]
 * @param b object to compare with [this@lte]
 * @returns true if [this@lte] is `lower than or equal to` [b] and false otherwise
 */
fun <A> List<A>.lte(OA: Order<A>, b: List<A>): Boolean =
  compare(OA, b) != GT

/**
 * Check if [this@gt] is `greater than` [b]
 *
 * @receiver object to compare with [b]
 * @param b object to compare with [this@gt]
 * @returns true if [this@gt] is `greater than` [b] and false otherwise
 */
fun <A> List<A>.gt(OA: Order<A>, b: List<A>): Boolean =
  compare(OA, b) == GT

/**
 * Check if [this@gte] is `greater than or equal to` [b]
 *
 * @receiver object to compare with [b]
 * @param b object to compare with [this@gte]
 * @returns true if [this@gte] is `greater than or equal to` [b] and false otherwise
 */
fun <A> List<A>.gte(OA: Order<A>, b: List<A>): Boolean =
  compare(OA, b) != LT

/**
 * Determines the maximum of [this@max] and [b] in terms of order.
 *
 * @receiver object to compare with [b]
 * @param b object to compare with [this@max]
 * @returns the maximum [this@max] if it is greater than [b] or [b] otherwise
 */
fun <A> List<A>.max(OA: Order<A>, b: List<A>): List<A> =
  if (gt(OA, b)) this else b

/**
 * Determines the minimum of [this@min] and [b] in terms of order.
 *
 * @receiver object to compare with [b]
 * @param b object to compare with [this@min]
 * @returns the minimum [this@min] if it is less than [b] or [b] otherwise
 */
fun <A> List<A>.min(OA: Order<A>, b: List<A>): List<A> =
  if (lt(OA, b)) this else b

/**
 * Sorts [this@sort] and [b] in terms of order.
 *
 * @receiver object to compare with [b]
 * @param b object to compare with [this@sort]
 * @returns a sorted [Tuple2] of [this@sort] and [b].
 */
fun <A> List<A>.sort(OA: Order<A>, b: List<A>): Tuple2<List<A>, List<A>> =
  if (gte(OA, b)) Tuple2(this, b) else Tuple2(b, this)

fun <A> Order.Companion.list(OA: Order<A>): Order<List<A>> =
  ListOrder(OA)

private class ListOrder<A>(private val OA: Order<A>) : Order<List<A>> {
  override fun List<A>.compare(b: List<A>): Ordering = compare(OA, b)
}

fun <A> Semigroup.Companion.list(): Semigroup<List<A>> =
  Monoid.list()

fun <A> Monoid.Companion.list(): Monoid<List<A>> =
  ListMonoid as Monoid<List<A>>

object ListMonoid : Monoid<List<Any?>> {
  override fun empty(): List<Any?> = emptyList()
  override fun List<Any?>.combine(b: List<Any?>): List<Any?> = this._plus(b)
}
