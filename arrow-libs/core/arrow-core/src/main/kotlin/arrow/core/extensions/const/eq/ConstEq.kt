package arrow.core.extensions.const.eq

import arrow.core.Const
import arrow.core.Const.Companion
import arrow.core.extensions.ConstEq
import arrow.typeclasses.Eq
import arrow.typeclasses.EqDeprecation
import kotlin.Boolean
import kotlin.Deprecated
import kotlin.Suppress
import kotlin.jvm.JvmName

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
fun <A, T> Const<A, T>.neqv(EQ: Eq<A>, arg1: Const<A, T>): Boolean = arrow.core.Const.eq<A,
  T>(EQ).run {
  this@neqv.neqv(arg1) as kotlin.Boolean
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(EqDeprecation)
inline fun <A, T> Companion.eq(EQ: Eq<A>): ConstEq<A, T> = object : arrow.core.extensions.ConstEq<A, T> {
  override fun EQ(): arrow.typeclasses.Eq<A> =
    EQ
}
