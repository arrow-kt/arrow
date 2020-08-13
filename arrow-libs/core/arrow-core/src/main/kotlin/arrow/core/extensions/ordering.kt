package arrow.core.extensions

import arrow.core.EQ
import arrow.core.Ordering
import arrow.extension
import arrow.typeclasses.Eq
import arrow.typeclasses.Hash
import arrow.typeclasses.Monoid
import arrow.typeclasses.Order
import arrow.typeclasses.Semigroup
import arrow.typeclasses.Show

@extension
interface OrderingEq : Eq<Ordering> {
  override fun Ordering.eqv(b: Ordering): Boolean = this === b
}

@extension
interface OrderingShow : Show<Ordering> {
  override fun Ordering.show(): String = toString()
}

@extension
interface OrderingHash : Hash<Ordering>, OrderingEq {
  override fun Ordering.hash(): Int = hashCode()
}

@extension
interface OrderingOrder : Order<Ordering> {
  override fun Ordering.compare(b: Ordering): Ordering = Ordering.fromInt(toInt().compareTo(b.toInt()))
}

@extension
interface OrderingSemigroup : Semigroup<Ordering> {
  override fun Ordering.combine(b: Ordering): Ordering = this + b
}

@extension
interface OrderingMonoid : Monoid<Ordering>, OrderingSemigroup {
  override fun empty(): Ordering = EQ
}
