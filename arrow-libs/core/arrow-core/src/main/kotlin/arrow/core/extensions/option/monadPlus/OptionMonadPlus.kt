package arrow.core.extensions.option.monadPlus

import arrow.Kind
import arrow.core.ForOption
import arrow.core.Option
import arrow.core.Option.Companion
import arrow.core.extensions.OptionMonadPlus

/**
 * cached extension
 */
@PublishedApi()
internal val monadPlus_singleton: OptionMonadPlus = object : arrow.core.extensions.OptionMonadPlus
    {}

@JvmName("zeroM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "Option.empty<A>()",
  "arrow.core.Option", "arrow.core.empty"
  ),
  DeprecationLevel.WARNING
)
fun <A> zeroM(): Option<A> = arrow.core.Option
   .monadPlus()
   .zeroM<A>() as arrow.core.Option<A>

@JvmName("plusM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "orElse { arg1 }",
  "arrow.core.Option", "arrow.core.orElse"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForOption, A>.plusM(arg1: Kind<ForOption, A>): Option<A> =
    arrow.core.Option.monadPlus().run {
  this@plusM.plusM<A>(arg1) as arrow.core.Option<A>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "MonadPlus typeclass is deprecated. Use concrete methods on Option",
  level = DeprecationLevel.WARNING
)
inline fun Companion.monadPlus(): OptionMonadPlus = monadPlus_singleton
