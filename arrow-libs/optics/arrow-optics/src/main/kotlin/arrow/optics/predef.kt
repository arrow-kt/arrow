package arrow.optics

import arrow.typeclasses.Monoid

@PublishedApi
internal fun <A> firstOptionMonoid(): Monoid<A?> = object : Monoid<A?> {
  override fun empty(): A? = null
  override fun A?.combine(b: A?): A? = this ?: b
}

internal fun <A> lastOptionMonoid(): Monoid<A?> = object : Monoid<A?> {
  override fun empty(): A? = null
  override fun A?.combine(b: A?): A? = b ?: this
}

private object BooleanOr : Monoid<Boolean> {
  override fun empty(): Boolean = false
  override fun Boolean.combine(b: Boolean): Boolean = this || b
}

internal fun Monoid.Companion.booleanOr(): Monoid<Boolean> =
  BooleanOr
