package arrow.core.extensions.mapk.align

import arrow.core.MapK
import arrow.core.MapK.Companion
import arrow.core.extensions.MapKAlign
import kotlin.Any
import kotlin.Deprecated
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val align_singleton: MapKAlign<Any?> = object : MapKAlign<Any?> {}

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
fun <K, A> empty(): MapK<K, A> = arrow.core.MapK
   .align<K>()
   .empty<A>() as arrow.core.MapK<K, A>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("Align typeclasses is deprecated. Use concrete methods on Map")
inline fun <K> Companion.align(): MapKAlign<K> = align_singleton as
    arrow.core.extensions.MapKAlign<K>
