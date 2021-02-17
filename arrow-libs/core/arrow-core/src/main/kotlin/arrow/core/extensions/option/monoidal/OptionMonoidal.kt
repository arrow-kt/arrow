package arrow.core.extensions.option.monoidal

import arrow.core.Option
import arrow.core.Option.Companion
import arrow.core.extensions.OptionMonoidal

/**
 * cached extension
 */
@PublishedApi()
internal val monoidal_singleton: OptionMonoidal = object : arrow.core.extensions.OptionMonoidal {}

@JvmName("identity")
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
    "arrow.core.Option",
    "arrow.core.empty"
  ),
  DeprecationLevel.WARNING
)
fun <A> identity(): Option<A> = arrow.core.Option
  .monoidal()
  .identity<A>() as arrow.core.Option<A>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "Monoidal typeclass is deprecated. Use concrete methods on Option",
  level = DeprecationLevel.WARNING
)
inline fun Companion.monoidal(): OptionMonoidal = monoidal_singleton
