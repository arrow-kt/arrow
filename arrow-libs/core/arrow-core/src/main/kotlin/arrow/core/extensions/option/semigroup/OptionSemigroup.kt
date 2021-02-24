package arrow.core.extensions.option.semigroup

import arrow.core.Option
import arrow.core.Option.Companion
import arrow.core.extensions.OptionSemigroup
import arrow.typeclasses.Semigroup

@JvmName("plus")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "combine(SG, arg1)",
    "arrow.core.combine"
  ),
  DeprecationLevel.WARNING
)
fun <A> Option<A>.plus(SG: Semigroup<A>, arg1: Option<A>): Option<A> =
  arrow.core.Option.semigroup<A>(SG).run {
    this@plus.plus(arg1) as arrow.core.Option<A>
  }

@JvmName("maybeCombine")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "combine(SG, arg1)",
    "arrow.core.combine"
  ),
  DeprecationLevel.WARNING
)
fun <A> Option<A>.maybeCombine(SG: Semigroup<A>, arg1: Option<A>): Option<A> =
  arrow.core.Option.semigroup<A>(SG).run {
    this@maybeCombine.maybeCombine(arg1) as arrow.core.Option<A>
  }

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "@extension projected functions are deprecated",
  ReplaceWith(
    "Semigroup.option<A>(EQ)",
    "arrow.core.option",
    "arrow.typeclasses.Semigroup"
  ),
  DeprecationLevel.WARNING
)
inline fun <A> Companion.semigroup(SG: Semigroup<A>): OptionSemigroup<A> = object :
  arrow.core.extensions.OptionSemigroup<A> {
  override fun SG(): arrow.typeclasses.Semigroup<A> =
    SG
}
