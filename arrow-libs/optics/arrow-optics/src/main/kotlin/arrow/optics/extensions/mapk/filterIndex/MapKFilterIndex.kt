package arrow.optics.extensions.mapk.filterIndex

import arrow.core.MapK
import arrow.core.MapK.Companion
import arrow.optics.PTraversal
import arrow.optics.extensions.MapKFilterIndex

/**
 * cached extension
 */
@PublishedApi()
internal val filterIndex_singleton: MapKFilterIndex<Any?, Any?> = object : MapKFilterIndex<Any?,
    Any?> {}

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
    arrow.core.MapK
   .filterIndex<K, V>()
   .filter(p) as arrow.optics.PTraversal<arrow.core.MapK<K, V>, arrow.core.MapK<K, V>, V, V>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "MapK is being deprecated. Use the instance for Map from the companion object of the typeclass.",
  ReplaceWith(
    "FilterIndex.map<K, V>()",
    "arrow.optics.map", "arrow.optics.typeclasses.FilterIndex"
  ),
  DeprecationLevel.WARNING
)
inline fun <K, V> Companion.filterIndex(): MapKFilterIndex<K, V> = filterIndex_singleton as
    arrow.optics.extensions.MapKFilterIndex<K, V>
