package arrow.core.extensions.option.zip

import arrow.Kind
import arrow.core.ForOption
import arrow.core.Option
import arrow.core.Option.Companion
import arrow.core.Tuple2
import arrow.core.extensions.OptionZip

/**
 * cached extension
 */
@PublishedApi()
internal val zip_singleton: OptionZip = object : arrow.core.extensions.OptionZip {}

@JvmName("zip")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "zip<B>(arg1)",
    "arrow.core.zip"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForOption, A>.zip(arg1: Kind<ForOption, B>): Option<Tuple2<A, B>> =
  arrow.core.Option.zip().run {
    this@zip.zip<A, B>(arg1) as arrow.core.Option<arrow.core.Tuple2<A, B>>
  }

@JvmName("zipWith")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "zip<B, C>(arg1, arg2)",
    "arrow.core.zip"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C> Kind<ForOption, A>.zipWith(arg1: Kind<ForOption, B>, arg2: Function2<A, B, C>): Option<C> =
  arrow.core.Option.zip().run {
    this@zipWith.zipWith<A, B, C>(arg1, arg2) as arrow.core.Option<C>
  }

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "Zip typeclass is deprecated. Use concrete methods on Option",
  level = DeprecationLevel.WARNING
)
inline fun Companion.zip(): OptionZip = zip_singleton
