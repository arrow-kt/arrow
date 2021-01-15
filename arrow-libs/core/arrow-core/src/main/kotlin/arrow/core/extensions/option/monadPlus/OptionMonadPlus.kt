package arrow.core.extensions.option.monadPlus

import arrow.Kind
import arrow.core.ForOption
import arrow.core.Option
import arrow.core.Option.Companion
import arrow.core.extensions.OptionMonadPlus
import kotlin.Deprecated
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

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
  "zeroM()",
  "arrow.core.Option.zeroM"
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
  "plusM(arg1)",
  "arrow.core.plusM"
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
inline fun Companion.monadPlus(): OptionMonadPlus = monadPlus_singleton
