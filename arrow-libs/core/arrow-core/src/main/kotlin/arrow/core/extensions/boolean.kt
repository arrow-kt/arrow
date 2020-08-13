package arrow.core.extensions

import arrow.core.Ordering
import arrow.typeclasses.Eq
import arrow.typeclasses.Hash
import arrow.typeclasses.Monoid
import arrow.typeclasses.Order
import arrow.typeclasses.Show

interface BooleanShow : Show<Boolean> {
  override fun Boolean.show(): String =
    this.toString()
}

interface BooleanEq : Eq<Boolean> {
  override fun Boolean.eqv(b: Boolean): Boolean = this == b
}

interface BooleanOrder : Order<Boolean> {
  override fun Boolean.compare(b: Boolean): Ordering = Ordering.fromInt(this.compareTo(b))
  override fun Boolean.compareTo(b: Boolean): Int = this.compareTo(b)
}

interface BooleanHash : Hash<Boolean>, BooleanEq {
  override fun Boolean.hash(): Int = this.hashCode()
}

fun Boolean.Companion.show(): Show<Boolean> =
  object : BooleanShow {}

fun Boolean.Companion.eq(): Eq<Boolean> =
  object : BooleanEq {}

fun Boolean.Companion.order(): Order<Boolean> =
  object : BooleanOrder {}

fun Boolean.Companion.hash(): Hash<Boolean> =
  object : BooleanHash {}

object AndMonoid : Monoid<Boolean> {
  override fun Boolean.combine(b: Boolean): Boolean = this && b
  override fun empty(): Boolean = true
}
