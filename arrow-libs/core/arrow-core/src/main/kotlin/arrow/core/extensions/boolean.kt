package arrow.core.extensions

import arrow.core.Ordering
import arrow.typeclasses.Eq
import arrow.typeclasses.EqDeprecation
import arrow.typeclasses.Hash
import arrow.typeclasses.Monoid
import arrow.typeclasses.Order
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

@Deprecated("Typeclass interface implementation will not be exposed directly anymore", ReplaceWith("Order.boolean()", "arrow.core.Order", "arrow.core.boolean"))
interface BooleanOrder : Order<Boolean> {
  override fun Boolean.compare(b: Boolean): Ordering = Ordering.fromInt(this.compareTo(b))
  override fun Boolean.compareTo(b: Boolean): Int = this.compareTo(b)
}

@Deprecated("Typeclass interface implementation will not be exposed directly anymore", ReplaceWith("Hash.boolean()", "arrow.core.Hash", "arrow.core.boolean"))
interface BooleanHash : Hash<Boolean>, BooleanEq {
  override fun Boolean.hash(): Int = this.hashCode()
}

@Deprecated(ShowDeprecation)
fun Boolean.Companion.show(): Show<Boolean> =
  object : BooleanShow {}

@Deprecated(EqDeprecation)
fun Boolean.Companion.eq(): Eq<Boolean> =
  object : BooleanEq {}

@Deprecated("Typeclass instance have been moved to the companion object of the typeclass", ReplaceWith("Order.boolean()", "arrow.core.Order", "arrow.core.boolean"))
fun Boolean.Companion.order(): Order<Boolean> =
  object : BooleanOrder {}

@Deprecated("Typeclass instance have been moved to the companion object of the typeclass", ReplaceWith("Hash.boolean()", "arrow.core.Hash", "arrow.core.boolean"))
fun Boolean.Companion.hash(): Hash<Boolean> =
  object : BooleanHash {}

@Deprecated("Typeclass interface implementation will not be exposed directly anymore", ReplaceWith("Monoid.boolean()", "arrow.core.Monoid", "arrow.core.boolean"))
object AndMonoid : Monoid<Boolean> {
  override fun Boolean.combine(b: Boolean): Boolean = this && b
  override fun empty(): Boolean = true
}
