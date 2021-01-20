package arrow.core.extensions.nonemptylist.unzip

import arrow.Kind
import arrow.core.ForNonEmptyList
import arrow.core.NonEmptyList.Companion
import arrow.core.Tuple2
import arrow.core.extensions.NonEmptyListUnzip
import kotlin.Deprecated
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val unzip_singleton: NonEmptyListUnzip = object : arrow.core.extensions.NonEmptyListUnzip
    {}

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
  "fix<Tuple2<A, B>>().unzip<A, B>()",
    "arrow.core.Tuple2", "arrow.core.fix", "arrow.core.unzip"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForNonEmptyList, Tuple2<A, B>>.unzip(): Tuple2<Kind<ForNonEmptyList, A>,
    Kind<ForNonEmptyList, B>> = arrow.core.NonEmptyList.unzip().run {
  this@unzip.unzip<A, B>() as arrow.core.Tuple2<arrow.Kind<arrow.core.ForNonEmptyList, A>,
    arrow.Kind<arrow.core.ForNonEmptyList, B>>
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
    "fix<C>().unzipWith<A, B, C>(arg1)",
    "arrow.core.fix", "arrow.core.unzipWith"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C> Kind<ForNonEmptyList, C>.unzipWith(arg1: Function1<C, Tuple2<A, B>>):
    Tuple2<Kind<ForNonEmptyList, A>, Kind<ForNonEmptyList, B>> =
    arrow.core.NonEmptyList.unzip().run {
  this@unzipWith.unzipWith<A, B, C>(arg1) as
    arrow.core.Tuple2<arrow.Kind<arrow.core.ForNonEmptyList, A>,
    arrow.Kind<arrow.core.ForNonEmptyList, B>>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "Unzip typeclass is deprecated. Use concrete methods on NonEmptyList",
  level = DeprecationLevel.WARNING)
inline fun Companion.unzip(): NonEmptyListUnzip = unzip_singleton
