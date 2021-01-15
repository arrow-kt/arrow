package arrow.core.extensions.id.unzip

import arrow.Kind
import arrow.core.ForId
import arrow.core.Id.Companion
import arrow.core.Tuple2
import arrow.core.extensions.IdUnzip
import kotlin.Deprecated
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val unzip_singleton: IdUnzip = object : arrow.core.extensions.IdUnzip {}

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
fun <A, B> Kind<ForId, Tuple2<A, B>>.unzip(): Tuple2<Kind<ForId, A>, Kind<ForId, B>> =
    arrow.core.Id.unzip().run {
  this@unzip.unzip<A, B>() as arrow.core.Tuple2<arrow.Kind<arrow.core.ForId, A>,
    arrow.Kind<arrow.core.ForId, B>>
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
  "unzipWith(arg1)",
  "arrow.core.unzipWith"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C> Kind<ForId, C>.unzipWith(arg1: Function1<C, Tuple2<A, B>>): Tuple2<Kind<ForId, A>,
    Kind<ForId, B>> = arrow.core.Id.unzip().run {
  this@unzipWith.unzipWith<A, B, C>(arg1) as arrow.core.Tuple2<arrow.Kind<arrow.core.ForId, A>,
    arrow.Kind<arrow.core.ForId, B>>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun Companion.unzip(): IdUnzip = unzip_singleton
