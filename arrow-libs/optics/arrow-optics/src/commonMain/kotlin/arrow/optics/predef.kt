package arrow.optics

import arrow.typeclasses.Monoid

@Suppress("ClassName")
internal object EMPTY_VALUE {
  @Suppress("UNCHECKED_CAST")
  inline fun <T> unbox(value: Any?): T =
    if (value === this) null as T else value as T
}

private object BooleanOr : Monoid<Boolean> {
  override fun empty(): Boolean = false
  override fun append(a: Boolean, b: Boolean): Boolean = a || b
}

internal fun Monoid.Companion.booleanOr(): Monoid<Boolean> =
  BooleanOr
