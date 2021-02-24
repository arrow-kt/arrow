package arrow.core.extensions.option.unzip

import arrow.Kind
import arrow.core.ForOption
import arrow.core.Option.Companion
import arrow.core.Tuple2
import arrow.core.extensions.OptionUnzip

/**
 * cached extension
 */
@PublishedApi()
internal val unzip_singleton: OptionUnzip = object : arrow.core.extensions.OptionUnzip {}

@JvmName("unzip")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "unzip()",
    "arrow.core.unzip"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForOption, Tuple2<A, B>>.unzip(): Tuple2<Kind<ForOption, A>, Kind<ForOption, B>> =
  arrow.core.Option.unzip().run {
    this@unzip.unzip<A, B>() as arrow.core.Tuple2<arrow.Kind<arrow.core.ForOption, A>,
      arrow.Kind<arrow.core.ForOption, B>>
  }

@JvmName("unzipWith")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "unzip(arg1)",
    "arrow.core.unzip"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C> Kind<ForOption, C>.unzipWith(arg1: Function1<C, Tuple2<A, B>>): Tuple2<Kind<ForOption,
    A>, Kind<ForOption, B>> = arrow.core.Option.unzip().run {
  this@unzipWith.unzipWith<A, B, C>(arg1) as arrow.core.Tuple2<arrow.Kind<arrow.core.ForOption, A>,
    arrow.Kind<arrow.core.ForOption, B>>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "Unzip typeclass is deprecated. Use concrete methods on Option",
  level = DeprecationLevel.WARNING
)
inline fun Companion.unzip(): OptionUnzip = unzip_singleton
