package arrow.core.extensions

import arrow.core.Ordering
import arrow.typeclasses.Eq
import arrow.typeclasses.Hash
import arrow.typeclasses.Order
import arrow.typeclasses.Show

@Deprecated("Typeclass interface implementation will not be exposed directly anymore", ReplaceWith("Show.char()", "arrow.core.Show", "arrow.core.char"))
interface CharShow : Show<Char> {
  override fun Char.show(): String =
    this.toString()
}

@Deprecated("Typeclass interface implementation will not be exposed directly anymore", ReplaceWith("Eq.char()", "arrow.core.Eq", "arrow.core.char"))
interface CharEq : Eq<Char> {
  override fun Char.eqv(b: Char): Boolean = this == b
}

@Deprecated("Typeclass interface implementation will not be exposed directly anymore", ReplaceWith("Order.char()", "arrow.core.Order", "arrow.core.char"))
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

@Deprecated("Typeclass instance have been moved to the companion object of the typeclass", ReplaceWith("Show.char()", "arrow.core.Show", "arrow.core.char"))
fun Char.Companion.show(): Show<Char> =
  object : CharShow {}

@Deprecated("Typeclass instance have been moved to the companion object of the typeclass", ReplaceWith("Eq.char()", "arrow.core.Eq", "arrow.core.char"))
fun Char.Companion.eq(): Eq<Char> =
  object : CharEq {}

@Deprecated("Typeclass instance have been moved to the companion object of the typeclass", ReplaceWith("Order.char()", "arrow.core.Order", "arrow.core.char"))
fun Char.Companion.order(): Order<Char> =
  object : CharOrder {}

@Deprecated("Typeclass instance have been moved to the companion object of the typeclass", ReplaceWith("Hash.char()", "arrow.core.Hash", "arrow.core.char"))
fun Char.Companion.hash(): Hash<Char> =
  object : CharHash {}
