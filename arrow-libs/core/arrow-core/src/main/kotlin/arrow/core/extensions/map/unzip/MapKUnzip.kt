package arrow.core.extensions.map.unzip

import arrow.Kind
import arrow.core.ForMapK
import arrow.core.Tuple2
import arrow.core.extensions.MapKUnzip
import kotlin.Any
import kotlin.Deprecated
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.collections.Map
import kotlin.jvm.JvmName

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
fun <K, A, B> Map<K, Tuple2<A, B>>.unzip(): Tuple2<Kind<Kind<ForMapK, K>, A>, Kind<Kind<ForMapK, K>,
    B>> = arrow.core.extensions.map.unzip.Map.unzip<K>().run {
  arrow.core.MapK(this@unzip).unzip<A, B>() as
    arrow.core.Tuple2<arrow.Kind<arrow.Kind<arrow.core.ForMapK, K>, A>,
    arrow.Kind<arrow.Kind<arrow.core.ForMapK, K>, B>>
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
  "unzip { (_, c) -> arg1(c) )",
  "arrow.core.unzip"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C> Map<K, C>.unzipWith(arg1: Function1<C, Tuple2<A, B>>): Tuple2<Kind<Kind<ForMapK,
    K>, A>, Kind<Kind<ForMapK, K>, B>> = arrow.core.extensions.map.unzip.Map.unzip<K>().run {
  arrow.core.MapK(this@unzipWith).unzipWith<A, B, C>(arg1) as
    arrow.core.Tuple2<arrow.Kind<arrow.Kind<arrow.core.ForMapK, K>, A>,
    arrow.Kind<arrow.Kind<arrow.core.ForMapK, K>, B>>
}

/**
 * cached extension
 */
@PublishedApi()
internal val unzip_singleton: MapKUnzip<Any?> = object : MapKUnzip<Any?> {}

object Map {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated("Unzip typeclasses is deprecated. Use concrete methods on Map")
  inline fun <K> unzip(): MapKUnzip<K> = unzip_singleton as arrow.core.extensions.MapKUnzip<K>}
