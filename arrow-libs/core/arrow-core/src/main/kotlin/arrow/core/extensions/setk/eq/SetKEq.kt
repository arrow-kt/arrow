package arrow.core.extensions.setk.eq

import arrow.core.SetK
import arrow.core.SetK.Companion
import arrow.core.extensions.SetKEq
import arrow.typeclasses.Eq
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
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "neqv(EQ, arg1)",
  "arrow.core.neqv"
  ),
  DeprecationLevel.WARNING
)
fun <A> SetK<A>.neqv(EQ: Eq<A>, arg1: SetK<A>): Boolean = arrow.core.SetK.eq<A>(EQ).run {
  this@neqv.neqv(arg1) as kotlin.Boolean
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <A> Companion.eq(EQ: Eq<A>): SetKEq<A> = object : arrow.core.extensions.SetKEq<A> {
    override fun EQ(): arrow.typeclasses.Eq<A> = EQ }
