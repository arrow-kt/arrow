package arrow.core.extensions.option.zip

import arrow.Kind
import arrow.core.ForOption
import arrow.core.Option
import arrow.core.Option.Companion
import arrow.core.Tuple2
import arrow.core.extensions.OptionZip
import kotlin.Deprecated
import kotlin.Function2
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

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
  "zip(arg1)",
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
  "zipWith(arg1, arg2)",
  "arrow.core.zipWith"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C> Kind<ForOption, A>.zipWith(arg1: Kind<ForOption, B>, arg2: Function2<A, B, C>):
    Option<C> = arrow.core.Option.zip().run {
  this@zipWith.zipWith<A, B, C>(arg1, arg2) as arrow.core.Option<C>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun Companion.zip(): OptionZip = zip_singleton
