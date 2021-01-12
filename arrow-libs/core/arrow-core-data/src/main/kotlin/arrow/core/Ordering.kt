package arrow.core

import arrow.typeclasses.Eq
import arrow.typeclasses.Hash
import arrow.typeclasses.Monoid
import arrow.typeclasses.Order
import arrow.typeclasses.Semigroup
import arrow.typeclasses.Show
import arrow.typeclasses.hashWithSalt

sealed class Ordering {
  override fun equals(other: Any?): Boolean = this === other // ref equality is fine because objects should be singletons

  override fun toString(): String = show()

  override fun hashCode(): Int = toInt()

  fun toInt(): Int = when (this) {
    LT -> -1
    GT -> 1
    EQ -> 0
  }

  operator fun plus(b: Ordering): Ordering = when (this) {
    LT -> LT
    EQ -> b
    GT -> GT
  }

  fun hash(): Int = hashWithSalt(hashCode())

  fun hashWithSalt(salt: Int): Int =
    salt.hashWithSalt(hashCode())

  fun compare(b: Ordering): Ordering = when (this) {
    is LT -> when (b) {
      is LT -> EQ
      else -> GT
    }
    is GT -> when (b) {
      is GT -> EQ
      else -> LT
    }
    is EQ -> b
  }

  fun compareTo(b: Ordering): Int = compare(b).toInt()

  fun eqv(other: Ordering): Boolean = compare(other) == EQ

  fun lt(b: Ordering): Boolean = compare(b) == LT

  fun lte(b: Ordering): Boolean = compare(b) != GT

  fun gt(b: Ordering): Boolean = compare(b) == GT

  fun gte(b: Ordering): Boolean = compare(b) != LT

  fun max(b: Ordering): Ordering = if (gt(b)) this else b

  fun min(b: Ordering): Ordering = if (lt(b)) this else b

  fun sort(b: Ordering): Tuple2<Ordering, Ordering> =
    if (gte(b)) Tuple2(this, b) else Tuple2(b, this)

  fun empty(): Ordering = EQ

  fun combine(b: Ordering): Ordering = this + b

  fun show(): String = when (this) {
    LT -> "LT"
    GT -> "GT"
    EQ -> "EQ"
  }

  companion object {
    fun fromInt(i: Int): Ordering = when (i) {
      0 -> EQ
      else -> if (i < 0) LT else GT
    }
  }
}

object LT : Ordering()
object GT : Ordering()
object EQ : Ordering()

fun Collection<Ordering>.combineAll(): Ordering =
  if (isEmpty()) OrderingMonoid.empty() else reduce { a, b -> a.combine(b) }

fun Eq.Companion.ordering(): Eq<Ordering> = OrderingEq

fun Hash.Companion.ordering(): Hash<Ordering> = OrderingHash

fun Semigroup.Companion.ordering(): Semigroup<Ordering> = OrderingMonoid

fun Monoid.Companion.ordering(): Monoid<Ordering> = OrderingMonoid

fun Order.Companion.ordering(): Order<Ordering> = OrderingOrder

fun Show.Companion.ordering(): Show<Ordering> = OrderingShow

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
private object OrderingEq : Eq<Ordering> {
  override fun Ordering.eqv(b: Ordering): Boolean =
    this.eqv(b)
}

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
private object OrderingHash : Hash<Ordering> {
  override fun Ordering.hash(): Int = this.hash()

  override fun Ordering.hashWithSalt(salt: Int): Int =
    this.hashWithSalt(salt)
}

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
private object OrderingMonoid : Monoid<Ordering> {
  override fun empty(): Ordering = EQ

  override fun Ordering.combine(b: Ordering): Ordering =
    this + b
}

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
private object OrderingOrder : Order<Ordering> {
  override fun Ordering.compare(b: Ordering): Ordering = this.compare(b)
}

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
private object OrderingShow : Show<Ordering> {
  override fun Ordering.show(): String = this.show()
}
