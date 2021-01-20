package arrow.core.extensions.nonemptylist.eq

import arrow.core.NonEmptyList
import arrow.core.NonEmptyList.Companion
import arrow.core.extensions.NonEmptyListEq
import arrow.typeclasses.Eq
import kotlin.Boolean
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
  "@extension projected functions are deprecated",
  ReplaceWith(
    "neqv<A>(EQ, arg1)",
    "arrow.core.neqv"))
fun <A> NonEmptyList<A>.neqv(EQ: Eq<A>, arg1: NonEmptyList<A>): Boolean =
    arrow.core.NonEmptyList.eq<A>(EQ).run {
  this@neqv.neqv(arg1) as kotlin.Boolean
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "@extension projected functions are deprecated",
  ReplaceWith(
    "Eq.nonEmptyList<A>(EQ)",
    "arrow.core.NonEmptyList", "arrow.typeclasses.Eq"
  ),
  DeprecationLevel.WARNING
)
inline fun <A> Companion.eq(EQ: Eq<A>): NonEmptyListEq<A> = object :
    arrow.core.extensions.NonEmptyListEq<A> { override fun EQ(): arrow.typeclasses.Eq<A> = EQ }
