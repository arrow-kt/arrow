package arrow.optics.extensions.map.filterIndex

import arrow.core.MapK
import arrow.optics.PTraversal
import arrow.optics.extensions.MapKFilterIndex

@JvmName("filter")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "arrow.optics.extensions package is being deprecated. Use the exposed function in the instance for Map from the companion object of the typeclass instead.",
  ReplaceWith(
    "FilterIndex.map<K, V>().filter(p)",
    "arrow.optics.map", "arrow.optics.typeclasses.FilterIndex"
  ),
  DeprecationLevel.WARNING
)
fun <K, V> filter(p: Function1<K, Boolean>): PTraversal<MapK<K, V>, MapK<K, V>, V, V> =
  arrow.optics.extensions.map.filterIndex.Map
    .filterIndex<K, V>()
    .filter(p) as arrow.optics.PTraversal<arrow.core.MapK<K, V>, arrow.core.MapK<K, V>, V, V>

/**
 * cached extension
 */
@PublishedApi()
internal val filterIndex_singleton: MapKFilterIndex<Any?, Any?> = object : MapKFilterIndex<Any?,
    Any?> {}

@Deprecated("Receiver Map object is deprecated, and it will be removed in 0.13.")
object Map {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  inline fun <K, V> filterIndex(): MapKFilterIndex<K, V> = filterIndex_singleton as
    arrow.optics.extensions.MapKFilterIndex<K, V>
}
