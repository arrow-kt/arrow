package arrow.core.extensions.option.repeat

import arrow.core.Option
import arrow.core.Option.Companion
import arrow.core.extensions.OptionRepeat

/**
 * cached extension
 */
@PublishedApi()
internal val repeat_singleton: OptionRepeat = object : arrow.core.extensions.OptionRepeat {}

@JvmName("repeat")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "Option(a)",
  "arrow.core.Option"
  ),
  DeprecationLevel.WARNING
)
fun <A> repeat(a: A): Option<A> = arrow.core.Option
   .repeat()
   .repeat<A>(a) as arrow.core.Option<A>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "Repeat typeclass is deprecated. Use concrete methods on Option",
  level = DeprecationLevel.WARNING
)
inline fun Companion.repeat(): OptionRepeat = repeat_singleton
