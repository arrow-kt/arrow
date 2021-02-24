package arrow.core.extensions

import arrow.core.EQ
import arrow.core.Ordering
import arrow.typeclasses.Eq
import arrow.typeclasses.Hash
import arrow.typeclasses.Monoid
import arrow.typeclasses.Order
import arrow.typeclasses.OrderDeprecation
import arrow.typeclasses.Semigroup
import arrow.typeclasses.Show

@Deprecated(OrderDeprecation)
interface OrderingEq : Eq<Ordering> {
  override fun Ordering.eqv(b: Ordering): Boolean = this === b
}

@Deprecated(OrderDeprecation)
interface OrderingShow : Show<Ordering> {
  override fun Ordering.show(): String = toString()
}

@Deprecated(OrderDeprecation)
interface OrderingHash : Hash<Ordering>, OrderingEq {
  override fun Ordering.hash(): Int = hashCode()
}

@Deprecated(OrderDeprecation)
interface OrderingOrder : Order<Ordering> {
  override fun Ordering.compare(b: Ordering): Ordering = Ordering.fromInt(toInt().compareTo(b.toInt()))
}

@Deprecated(OrderDeprecation)
interface OrderingSemigroup : Semigroup<Ordering> {
  override fun Ordering.combine(b: Ordering): Ordering = this + b
}

@Deprecated(OrderDeprecation)
interface OrderingMonoid : Monoid<Ordering>, OrderingSemigroup {
  override fun empty(): Ordering = EQ
}
