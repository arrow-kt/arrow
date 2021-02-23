package arrow.optics.extensions.map.index

import arrow.core.MapK
import arrow.optics.PLens
import arrow.optics.POptional
import arrow.optics.extensions.MapKIndex

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
  arrow.optics.extensions.map.index.Map.index<K, V>().run {
    this@index.index<T>(i) as arrow.optics.POptional<T, T, V, V>
  }

/**
 * cached extension
 */
@PublishedApi()
internal val index_singleton: MapKIndex<Any?, Any?> = object : MapKIndex<Any?, Any?> {}

@Deprecated("Receiver Map object is deprecated, and it will be removed in 0.13.")
object Map {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated(
    "Typeclass instance have been moved to the companion object of the typeclass.",
    ReplaceWith(
      "Index.map<K, V>()",
      "arrow.optics.map", "arrow.optics.typeclasses.Index"
    ),
    DeprecationLevel.WARNING
  )
  inline fun <K, V> index(): MapKIndex<K, V> = index_singleton as
    arrow.optics.extensions.MapKIndex<K, V>
}
