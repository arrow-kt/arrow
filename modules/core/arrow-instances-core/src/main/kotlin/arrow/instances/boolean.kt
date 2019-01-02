package arrow.instances

import arrow.typeclasses.Eq
import arrow.typeclasses.Hash
import arrow.typeclasses.Show

interface BooleanShowInstance : Show<Boolean> {
  override fun Boolean.show(): String =
    this.toString()
}

interface BooleanEqInstance : Eq<Boolean> {
  override fun Boolean.eqv(b: Boolean): Boolean = this == b
}

interface BooleanHashInstance : Hash<Boolean>, BooleanEqInstance {
  override fun Boolean.hash(): Int = this.hashCode()
}

fun Boolean.Companion.show(): Show<Boolean> =
  object : BooleanShowInstance {}

fun Boolean.Companion.eq(): Eq<Boolean> =
  object : BooleanEqInstance {}

fun Boolean.Companion.hash(): Hash<Boolean> =
  object : BooleanHashInstance {}
