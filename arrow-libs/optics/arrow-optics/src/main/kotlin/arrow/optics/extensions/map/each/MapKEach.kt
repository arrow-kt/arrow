package arrow.optics.extensions.map.each

import arrow.core.MapK
import arrow.optics.PTraversal
import arrow.optics.extensions.MapKEach

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
    "arrow.optics.Traversal", "arrow.optics.map"
  ),
  DeprecationLevel.WARNING
)
fun <K, V> each(): PTraversal<MapK<K, V>, MapK<K, V>, V, V> = arrow.optics.extensions.map.each.Map
  .each<K, V>()
  .each() as arrow.optics.PTraversal<arrow.core.MapK<K, V>, arrow.core.MapK<K, V>, V, V>

/**
 * cached extension
 */
@PublishedApi()
internal val each_singleton: MapKEach<Any?, Any?> = object : MapKEach<Any?, Any?> {}

@Deprecated("Receiver Map object is deprecated, and it will be removed in 0.13.")
object Map {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated(
    "Each is being deprecated. Use the instance for Map from Traversal's companion object instead.",
    ReplaceWith(
      "Traversal.map<K, V>()",
      "arrow.optics.Traversal", "arrow.optics.map"
    ),
    DeprecationLevel.WARNING
  )
  inline fun <K, V> each(): MapKEach<K, V> = each_singleton as arrow.optics.extensions.MapKEach<K, V>
}
