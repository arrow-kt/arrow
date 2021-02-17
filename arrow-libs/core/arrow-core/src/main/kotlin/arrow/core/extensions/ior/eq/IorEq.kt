package arrow.core.extensions.ior.eq

import arrow.core.Ior
import arrow.core.Ior.Companion
import arrow.core.extensions.IorEq
import arrow.typeclasses.Eq
import arrow.typeclasses.EqDeprecation

@JvmName("neqv")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  EqDeprecation,
  ReplaceWith("this != arg1"),
  DeprecationLevel.WARNING
)
fun <L, R> Ior<L, R>.neqv(
  EQL: Eq<L>,
  EQR: Eq<R>,
  arg1: Ior<L, R>
): Boolean = arrow.core.Ior.eq<L, R>(EQL, EQR).run {
  this@neqv.neqv(arg1) as kotlin.Boolean
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  EqDeprecation,
  level = DeprecationLevel.WARNING
)
inline fun <L, R> Companion.eq(EQL: Eq<L>, EQR: Eq<R>): IorEq<L, R> = object :
  arrow.core.extensions.IorEq<L, R> {
  override fun EQL(): arrow.typeclasses.Eq<L> = EQL

  override fun EQR(): arrow.typeclasses.Eq<R> = EQR
}
