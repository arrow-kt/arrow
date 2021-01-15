package arrow.core.extensions.map.zip

import arrow.core.Tuple2
import arrow.core.extensions.MapKZip
import kotlin.Any
import kotlin.Deprecated
import kotlin.Function2
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.collections.Map
import kotlin.jvm.JvmName

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
fun <K, A, B> Map<K, A>.zip(arg1: Map<K, B>): Map<K, Tuple2<A, B>> =
    arrow.core.extensions.map.zip.Map.zip<K>().run {
  arrow.core.MapK(this@zip).zip<A, B>(arrow.core.MapK(arg1)) as kotlin.collections.Map<K,
    arrow.core.Tuple2<A, B>>
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
  "zip(arg1) { _, a, b -> arg2(a, b) } ",
  "arrow.core.zip"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C> Map<K, A>.zipWith(arg1: Map<K, B>, arg2: Function2<A, B, C>): Map<K, C> =
    arrow.core.extensions.map.zip.Map.zip<K>().run {
  arrow.core.MapK(this@zipWith).zipWith<A, B, C>(arrow.core.MapK(arg1), arg2) as
    kotlin.collections.Map<K, C>
}

/**
 * cached extension
 */
@PublishedApi()
internal val zip_singleton: MapKZip<Any?> = object : MapKZip<Any?> {}

object Map {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated("Unzip typeclasses is deprecated. Use concrete methods on Map")
  inline fun <K> zip(): MapKZip<K> = zip_singleton as arrow.core.extensions.MapKZip<K>}
