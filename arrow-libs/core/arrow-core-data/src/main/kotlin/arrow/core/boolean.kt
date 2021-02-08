package arrow.core

import arrow.typeclasses.Hash
import arrow.typeclasses.Monoid

private object BooleanHash : Hash<Boolean> {
  override fun Boolean.hash(): Int = this.hashCode()
}

fun Hash.Companion.boolean(): Hash<Boolean> =
  BooleanHash

private object AndMonoid : Monoid<Boolean> {
  override fun Boolean.combine(b: Boolean): Boolean = this && b
  override fun empty(): Boolean = true
}

fun Monoid.Companion.boolean(): Monoid<Boolean> =
  AndMonoid
