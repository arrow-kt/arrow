package arrow.core.extensions.setk.eq

import arrow.core.SetK
import arrow.core.SetK.Companion
import arrow.core.extensions.SetKEq
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
fun <A> SetK<A>.neqv(EQ: Eq<A>, arg1: SetK<A>): Boolean = arrow.core.SetK.eq<A>(EQ).run {
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
inline fun <A> Companion.eq(EQ: Eq<A>): SetKEq<A> = object : arrow.core.extensions.SetKEq<A> {
  override fun EQ(): arrow.typeclasses.Eq<A> = EQ
}
