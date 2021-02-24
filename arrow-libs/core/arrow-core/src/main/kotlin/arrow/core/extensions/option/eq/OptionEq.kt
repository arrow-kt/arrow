package arrow.core.extensions.option.eq

import arrow.core.Option
import arrow.core.Option.Companion
import arrow.core.extensions.OptionEq
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
fun <A> Option<A>.neqv(EQ: Eq<A>, arg1: Option<A>): Boolean = arrow.core.Option.eq<A>(EQ).run {
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
inline fun <A> Companion.eq(EQ: Eq<A>): OptionEq<A> = object : arrow.core.extensions.OptionEq<A> {
  override fun EQ(): arrow.typeclasses.Eq<A> = EQ
}
