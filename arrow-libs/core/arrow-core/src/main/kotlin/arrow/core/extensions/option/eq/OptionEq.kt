package arrow.core.extensions.option.eq

import arrow.core.Option
import arrow.core.Option.Companion
import arrow.core.extensions.OptionEq
import arrow.typeclasses.Eq

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
    "this.compareTo(arg1) != 0",
    "arrow.core.compareTo"
  ),
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
  "Eq typeclass is deprecated. Use concrete methods on Option",
  level = DeprecationLevel.WARNING
)
inline fun <A> Companion.eq(EQ: Eq<A>): OptionEq<A> = object : arrow.core.extensions.OptionEq<A> {
    override fun EQ(): arrow.typeclasses.Eq<A> = EQ }
