package arrow.instances

import arrow.typeclasses.*

interface CharShowInstance : Show<Char> {
  override fun Char.show(): String =
    this.toString()
}

interface CharEqInstance : Eq<Char> {
  override fun Char.eqv(b: Char): Boolean = this == b
}

interface CharSemigroupInstance : Semigroup<Char> {
  override fun Char.combine(b: Char): Char = this + b
}

interface CharOrderInstance : Order<Char> {
  override fun Char.eqv(b: Char): Boolean = this == b

  override fun Char.compare(b: Char): Int =
    if (this < b) -1 else if (this > b) 1 else 0

  override fun Char.lt(b: Char): Boolean = this < b

  override fun Char.lte(b: Char): Boolean = this <= b

  override fun Char.gt(b: Char): Boolean = this > b

  override fun Char.gte(b: Char): Boolean = this >= b

  override fun Char.neqv(b: Char): Boolean = this != b
}

fun Char.Companion.show(): Show<Char> =
  object : CharShowInstance {}

fun Char.Companion.eq(): Eq<Char> =
  object : CharEqInstance {}

fun Char.Companion.order(): Order<Char> =
  object : CharOrderInstance {}
