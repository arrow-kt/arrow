package arrow.optics.extensions.map.at

import arrow.core.MapK
import arrow.core.Option
import arrow.optics.PLens
import arrow.optics.extensions.MapKAt

@JvmName("at")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "arrow.optics.extensions package is being deprecated, and it will be removed in 0.13.",
  ReplaceWith(
    "this compose At.map<K, V>().at(i)",
    "arrow.optics.map", "arrow.optics.typeclasses.At", "arrow.optics.compose"
  ),
  level = DeprecationLevel.WARNING
)
fun <K, V, T> PLens<T, T, MapK<K, V>, MapK<K, V>>.at(i: K): PLens<T, T, Option<V>, Option<V>> =
  arrow.optics.extensions.map.at.Map.at<K, V>().run {
    this@at.at<T>(i) as arrow.optics.PLens<T, T, arrow.core.Option<V>, arrow.core.Option<V>>
  }

/**
 * cached extension
 */
@PublishedApi()
internal val at_singleton: MapKAt<Any?, Any?> = object : MapKAt<Any?, Any?> {}

@Deprecated("Receiver Map object is deprecated, and it will be removed in 0.13.")
object Map {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated(
    "Typeclass instance have been moved to the companion object of the typeclass.",
    ReplaceWith(
      "At.map<K, V>()",
      "arrow.optics.map", "arrow.optics.typeclasses.At"
    ),
    DeprecationLevel.WARNING
  )
  inline fun <K, V> at(): MapKAt<K, V> = at_singleton as arrow.optics.extensions.MapKAt<K, V>
}
