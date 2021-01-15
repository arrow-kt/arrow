package arrow.core.extensions.option.semigroup

import arrow.core.Option
import arrow.core.Option.Companion
import arrow.core.extensions.OptionSemigroup
import arrow.typeclasses.Semigroup
import kotlin.Deprecated
import kotlin.Suppress
import kotlin.jvm.JvmName

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
  "plus(SG, arg1)",
  "arrow.core.plus"
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
  "maybeCombine(SG, arg1)",
  "arrow.core.maybeCombine"
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
inline fun <A> Companion.semigroup(SG: Semigroup<A>): OptionSemigroup<A> = object :
    arrow.core.extensions.OptionSemigroup<A> { override fun SG(): arrow.typeclasses.Semigroup<A> =
    SG }
