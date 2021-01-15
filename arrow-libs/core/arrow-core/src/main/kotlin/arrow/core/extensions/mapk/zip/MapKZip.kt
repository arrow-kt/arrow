package arrow.core.extensions.mapk.zip

import arrow.Kind
import arrow.core.ForMapK
import arrow.core.MapK
import arrow.core.MapK.Companion
import arrow.core.Tuple2
import arrow.core.extensions.MapKZip
import kotlin.Any
import kotlin.Deprecated
import kotlin.Function2
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val zip_singleton: MapKZip<Any?> = object : MapKZip<Any?> {}

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
fun <K, A, B> Kind<Kind<ForMapK, K>, A>.zip(arg1: Kind<Kind<ForMapK, K>, B>): MapK<K, Tuple2<A, B>> =
    arrow.core.MapK.zip<K>().run {
  this@zip.zip<A, B>(arg1) as arrow.core.MapK<K, arrow.core.Tuple2<A, B>>
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
fun <K, A, B, C> Kind<Kind<ForMapK, K>, A>.zipWith(
  arg1: Kind<Kind<ForMapK, K>, B>,
  arg2: Function2<A, B, C>
): MapK<K, C> = arrow.core.MapK.zip<K>().run {
  this@zipWith.zipWith<A, B, C>(arg1, arg2) as arrow.core.MapK<K, C>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("Unzip typeclasses is deprecated. Use concrete methods on Map")
inline fun <K> Companion.zip(): MapKZip<K> = zip_singleton as arrow.core.extensions.MapKZip<K>
