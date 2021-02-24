package arrow.core.extensions.option.align

import arrow.core.Option
import arrow.core.Option.Companion
import arrow.core.extensions.OptionAlign

/**
 * cached extension
 */
@PublishedApi()
internal val align_singleton: OptionAlign = object : arrow.core.extensions.OptionAlign {}

@JvmName("empty")
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
    "arrow.core.Option"
  ),
  DeprecationLevel.WARNING
)
fun <A> empty(): Option<A> = arrow.core.Option
  .align()
  .empty<A>() as arrow.core.Option<A>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "Align typeclass is deprecated. Use concrete methods on Option",
  level = DeprecationLevel.WARNING
)
inline fun Companion.align(): OptionAlign = align_singleton
