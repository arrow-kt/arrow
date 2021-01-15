package arrow.optics.extensions.mapk.each

import arrow.core.MapK
import arrow.core.MapK.Companion
import arrow.optics.PTraversal
import arrow.optics.extensions.MapKEach

/**
 * cached extension
 */
@PublishedApi()
internal val each_singleton: MapKEach<Any?, Any?> = object : MapKEach<Any?, Any?> {}

@JvmName("each")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "Each is being deprecated. Use the instance for Map from Traversal's companion object instead.",
  ReplaceWith(
    "Traversal.map<K, V>()",
    "arrow.optics.Traversal", "arrow.optics.map"),
  DeprecationLevel.WARNING
)
fun <K, V> each(): PTraversal<MapK<K, V>, MapK<K, V>, V, V> = arrow.core.MapK
   .each<K, V>()
   .each() as arrow.optics.PTraversal<arrow.core.MapK<K, V>, arrow.core.MapK<K, V>, V, V>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "Each is being deprecated. Use the instance for Map from Traversal's companion object instead.",
  ReplaceWith(
    "Traversal.map<K, V>()",
    "arrow.optics.Traversal", "arrow.optics.map"),
  DeprecationLevel.WARNING
)
inline fun <K, V> Companion.each(): MapKEach<K, V> =
  each_singleton as arrow.optics.extensions.MapKEach<K, V>
