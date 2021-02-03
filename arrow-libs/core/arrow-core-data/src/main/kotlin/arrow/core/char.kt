package arrow.core

import arrow.typeclasses.Eq
import arrow.typeclasses.Hash
import arrow.typeclasses.Order

private object CharEq : Eq<Char> {
  override fun Char.eqv(b: Char): Boolean = this == b
}

private object CharOrder : Order<Char> {
  override fun Char.compare(b: Char): Ordering =
    Ordering.fromInt(this.compareTo(b))

  override fun Char.compareTo(b: Char): Int =
    this.compareTo(b)
}

private object CharHash : Hash<Char> {
  override fun Char.hash(): Int = this.hashCode()
}

fun Eq.Companion.char(): Eq<Char> =
  CharEq

fun Order.Companion.char(): Order<Char> =
  CharOrder

fun Hash.Companion.char(): Hash<Char> =
  CharHash
