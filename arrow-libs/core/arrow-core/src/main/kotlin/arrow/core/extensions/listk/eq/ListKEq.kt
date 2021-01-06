package arrow.core.extensions.listk.eq

import arrow.Kind
import arrow.core.ForListK
import arrow.core.ListK.Companion
import arrow.core.extensions.ListKEq
import arrow.typeclasses.Eq
import kotlin.Boolean
import kotlin.Suppress
import kotlin.jvm.JvmName

@JvmName("eqv")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("neqv(EQL, EQR, arg1)", "arrow.core.neqv"))
fun <A> Kind<ForListK, A>.eqv(EQ: Eq<A>, arg1: Kind<ForListK, A>): Boolean =
    arrow.core.ListK.eq<A>(EQ).run {
  this@eqv.eqv(arg1) as kotlin.Boolean
}

@JvmName("neqv")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("neqv(EQL, EQR, arg1)", "arrow.core.neqv"))
fun <A> Kind<ForListK, A>.neqv(EQ: Eq<A>, arg1: Kind<ForListK, A>): Boolean =
    arrow.core.ListK.eq<A>(EQ).run {
  this@neqv.neqv(arg1) as kotlin.Boolean
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("listEq(EQ)", "arrow.core.listEq"))
inline fun <A> Companion.eq(EQ: Eq<A>): ListKEq<A> = object : arrow.core.extensions.ListKEq<A> {
    override fun EQ(): arrow.typeclasses.Eq<A> = EQ }
