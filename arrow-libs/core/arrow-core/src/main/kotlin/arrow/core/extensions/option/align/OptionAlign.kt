package arrow.core.extensions.option.align

import arrow.core.Option
import arrow.core.Option.Companion
import arrow.core.extensions.OptionAlign
import kotlin.Deprecated
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

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
  "empty()",
  "arrow.core.Option.empty"
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
inline fun Companion.align(): OptionAlign = align_singleton
