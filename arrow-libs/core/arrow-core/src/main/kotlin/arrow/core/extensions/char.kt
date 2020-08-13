package arrow.core.extensions

import arrow.core.Ordering
import arrow.typeclasses.Eq
import arrow.typeclasses.Hash
import arrow.typeclasses.Order
import arrow.typeclasses.Show

interface CharShow : Show<Char> {
  override fun Char.show(): String =
    this.toString()
}

interface CharEq : Eq<Char> {
  override fun Char.eqv(b: Char): Boolean = this == b
}

interface CharOrder : Order<Char> {
  override fun Char.compare(b: Char): Ordering =
    Ordering.fromInt(this.compareTo(b))

  override fun Char.compareTo(b: Char): Int =
    this.compareTo(b)
}

interface CharHash : Hash<Char>, CharEq {
  override fun Char.hash(): Int = this.hashCode()
}

fun Char.Companion.show(): Show<Char> =
  object : CharShow {}

fun Char.Companion.eq(): Eq<Char> =
  object : CharEq {}

fun Char.Companion.order(): Order<Char> =
  object : CharOrder {}

fun Char.Companion.hash(): Hash<Char> =
  object : CharHash {}
