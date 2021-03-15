package arrow.core

import arrow.typeclasses.Monoid

private object AndMonoid : Monoid<Boolean> {
  override fun Boolean.combine(b: Boolean): Boolean = this && b
  override fun empty(): Boolean = true
}

fun Monoid.Companion.boolean(): Monoid<Boolean> =
  AndMonoid
