package arrow.core.extensions

import arrow.core.Ordering
import arrow.typeclasses.Eq
import arrow.typeclasses.EqDeprecation
import arrow.typeclasses.Hash
import arrow.typeclasses.Order
import arrow.typeclasses.OrderDeprecation
import arrow.typeclasses.Show
import arrow.typeclasses.ShowDeprecation

@Deprecated(ShowDeprecation)
interface CharShow : Show<Char> {
  override fun Char.show(): String =
    this.toString()
}

@Deprecated(EqDeprecation)
interface CharEq : Eq<Char> {
  override fun Char.eqv(b: Char): Boolean = this == b
}

@Deprecated(OrderDeprecation)
interface CharOrder : Order<Char> {
  override fun Char.compare(b: Char): Ordering =
    Ordering.fromInt(this.compareTo(b))

  override fun Char.compareTo(b: Char): Int =
    this.compareTo(b)
}

@Deprecated("Typeclass interface implementation will not be exposed directly anymore", ReplaceWith("Hash.char()", "arrow.core.Hash", "arrow.core.char"))
interface CharHash : Hash<Char>, CharEq {
  override fun Char.hash(): Int = this.hashCode()
}

@Deprecated(ShowDeprecation)
fun Char.Companion.show(): Show<Char> =
  object : CharShow {}

@Deprecated(EqDeprecation)
fun Char.Companion.eq(): Eq<Char> =
  object : CharEq {}

@Deprecated(OrderDeprecation)
fun Char.Companion.order(): Order<Char> =
  object : CharOrder {}

@Deprecated("Typeclass instance have been moved to the companion object of the typeclass", ReplaceWith("Hash.char()", "arrow.core.Hash", "arrow.core.char"))
fun Char.Companion.hash(): Hash<Char> =
  object : CharHash {}
