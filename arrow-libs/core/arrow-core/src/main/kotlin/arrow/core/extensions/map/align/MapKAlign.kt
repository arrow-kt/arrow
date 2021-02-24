package arrow.core.extensions.map.align

import arrow.core.extensions.MapKAlign
import kotlin.Any
import kotlin.Deprecated
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.collections.Map
import kotlin.jvm.JvmName

@JvmName("empty")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith("emptyMap<K, A>()"),
  DeprecationLevel.WARNING
)
fun <K, A> empty(): Map<K, A> = arrow.core.extensions.map.align.Map
  .align<K>()
  .empty<A>() as kotlin.collections.Map<K, A>

/**
 * cached extension
 */
@PublishedApi()
internal val align_singleton: MapKAlign<Any?> = object : MapKAlign<Any?> {}

object Map {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated("Align typeclasses is deprecated. Use concrete methods on Map")
  inline fun <K> align(): MapKAlign<K> = align_singleton as arrow.core.extensions.MapKAlign<K>
}
