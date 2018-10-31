package arrow.instances

import arrow.typeclasses.Eq
import arrow.typeclasses.Show

interface BooleanShowInstance : Show<Boolean> {
  override fun Boolean.show(): String =
    this.toString()
}

interface BooleanEqInstance : Eq<Boolean> {
  override fun Boolean.eqv(b: Boolean): Boolean = this == b
}

fun Boolean.Companion.show(): Show<Boolean> =
  object : BooleanShowInstance {}

fun Boolean.Companion.eq(): Eq<Boolean> =
  object : BooleanEqInstance {}
