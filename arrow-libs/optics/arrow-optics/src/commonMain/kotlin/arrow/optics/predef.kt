package arrow.optics

import arrow.typeclasses.Monoid

@Suppress("ClassName")
internal object EMPTY_VALUE {
  @Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
  inline fun <T> unbox(value: Any?): T =
    if (value === this) null as T else value as T
}

private object BooleanOr : Monoid<Boolean> {
  override fun empty(): Boolean = false
  override fun Boolean.combine(b: Boolean): Boolean = this || b
}

internal fun Monoid.Companion.booleanOr(): Monoid<Boolean> =
  BooleanOr
