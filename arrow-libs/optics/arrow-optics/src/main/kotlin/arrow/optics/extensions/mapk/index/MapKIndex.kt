package arrow.optics.extensions.mapk.index

import arrow.core.MapK
import arrow.core.MapK.Companion
import arrow.optics.PLens
import arrow.optics.POptional
import arrow.optics.extensions.MapKIndex

/**
 * cached extension
 */
@PublishedApi()
internal val index_singleton: MapKIndex<Any?, Any?> = object : MapKIndex<Any?, Any?> {}

@JvmName("index")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "arrow.optics.extensions package is being deprecated, and it will be removed in 0.13.",
  ReplaceWith(
    "this compose Index.map<K, V>().index(i)",
    "arrow.optics.map", "arrow.optics.typeclasses.Index", "arrow.optics.compose"
  ),
  level = DeprecationLevel.WARNING
)
fun <K, V, T> PLens<T, T, MapK<K, V>, MapK<K, V>>.index(i: K): POptional<T, T, V, V> =
  arrow.core.MapK.index<K, V>().run {
    this@index.index<T>(i) as arrow.optics.POptional<T, T, V, V>
  }

@JvmName("get")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "arrow.optics.extensions package is being deprecated, and it will be removed in 0.13.",
  ReplaceWith(
    "this compose Index.map<K, V>().get(i)",
    "arrow.optics.map", "arrow.optics.typeclasses.Index", "arrow.optics.compose"
  ),
  level = DeprecationLevel.WARNING
)
operator fun <K, V, T> PLens<T, T, MapK<K, V>, MapK<K, V>>.get(i: K): POptional<T, T, V, V> =
  arrow.core.MapK.index<K, V>().run {
    this@get.get<T>(i) as arrow.optics.POptional<T, T, V, V>
  }

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "MapK is being deprecated. Use the instance for Map from the companion object of the typeclass.",
  ReplaceWith(
    "Index.map<K, V>()",
    "arrow.optics.map", "arrow.optics.typeclasses.Index"
  ),
  DeprecationLevel.WARNING
)
inline fun <K, V> Companion.index(): MapKIndex<K, V> = index_singleton as
  arrow.optics.extensions.MapKIndex<K, V>
