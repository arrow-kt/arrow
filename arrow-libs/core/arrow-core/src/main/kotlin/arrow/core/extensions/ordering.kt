package arrow.core.extensions

import arrow.core.EQ
import arrow.core.Ordering
import arrow.typeclasses.Eq
import arrow.typeclasses.Hash
import arrow.typeclasses.Monoid
import arrow.typeclasses.Order
import arrow.typeclasses.Semigroup
import arrow.typeclasses.Show

interface OrderingEq : Eq<Ordering> {
  override fun Ordering.eqv(b: Ordering): Boolean = this === b
}

interface OrderingShow : Show<Ordering> {
  override fun Ordering.show(): String = toString()
}

interface OrderingHash : Hash<Ordering>, OrderingEq {
  override fun Ordering.hash(): Int = hashCode()
}

interface OrderingOrder : Order<Ordering> {
  override fun Ordering.compare(b: Ordering): Ordering = Ordering.fromInt(toInt().compareTo(b.toInt()))
}

interface OrderingSemigroup : Semigroup<Ordering> {
  override fun Ordering.combine(b: Ordering): Ordering = this + b
}

interface OrderingMonoid : Monoid<Ordering>, OrderingSemigroup {
  override fun empty(): Ordering = EQ
}
