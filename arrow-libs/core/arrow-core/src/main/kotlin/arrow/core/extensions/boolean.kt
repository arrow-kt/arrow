package arrow.core.extensions

import arrow.core.Ordering
import arrow.typeclasses.Eq
import arrow.typeclasses.EqDeprecation
import arrow.typeclasses.Hash
import arrow.typeclasses.HashDeprecation
import arrow.typeclasses.Monoid
import arrow.typeclasses.Order
import arrow.typeclasses.OrderDeprecation
import arrow.typeclasses.Show
import arrow.typeclasses.ShowDeprecation

@Deprecated(ShowDeprecation)
interface BooleanShow : Show<Boolean> {
  override fun Boolean.show(): String =
    this.toString()
}

@Deprecated(EqDeprecation)
interface BooleanEq : Eq<Boolean> {
  override fun Boolean.eqv(b: Boolean): Boolean = this == b
}

@Deprecated(OrderDeprecation)
interface BooleanOrder : Order<Boolean> {
  override fun Boolean.compare(b: Boolean): Ordering = Ordering.fromInt(this.compareTo(b))
  override fun Boolean.compareTo(b: Boolean): Int = this.compareTo(b)
}

@Deprecated(HashDeprecation)
interface BooleanHash : Hash<Boolean>, BooleanEq {
  override fun Boolean.hash(): Int = this.hashCode()
}

@Deprecated(ShowDeprecation)
fun Boolean.Companion.show(): Show<Boolean> =
  object : BooleanShow {}

@Deprecated(EqDeprecation)
fun Boolean.Companion.eq(): Eq<Boolean> =
  object : BooleanEq {}

@Deprecated(OrderDeprecation)
fun Boolean.Companion.order(): Order<Boolean> =
  object : BooleanOrder {}

@Deprecated(HashDeprecation)
fun Boolean.Companion.hash(): Hash<Boolean> =
  object : BooleanHash {}

@Deprecated("Typeclass interface implementation will not be exposed directly anymore", ReplaceWith("Monoid.boolean()", "arrow.core.Monoid", "arrow.core.boolean"))
object AndMonoid : Monoid<Boolean> {
  override fun Boolean.combine(b: Boolean): Boolean = this && b
  override fun empty(): Boolean = true
}
