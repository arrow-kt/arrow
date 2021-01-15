package arrow.core.extensions.map.unalign

import arrow.Kind
import arrow.core.ForMapK
import arrow.core.Ior
import arrow.core.Tuple2
import arrow.core.extensions.MapKUnalign
import kotlin.Any
import kotlin.Deprecated
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.collections.Map
import kotlin.jvm.JvmName

@JvmName("unalign")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "arg0.unalign()",
  "arrow.core.unalign"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B> unalign(arg0: Map<K, Ior<A, B>>): Tuple2<Kind<Kind<ForMapK, K>, A>, Kind<Kind<ForMapK,
    K>, B>> = arrow.core.extensions.map.unalign.Map
   .unalign<K>()
   .unalign<A, B>(arrow.core.MapK(arg0)) as
    arrow.core.Tuple2<arrow.Kind<arrow.Kind<arrow.core.ForMapK, K>, A>,
    arrow.Kind<arrow.Kind<arrow.core.ForMapK, K>, B>>

@JvmName("unalignWith")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "arg0.unalign { (_, c) -> arg1(c) }",
  "arrow.core.unalign"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C> unalignWith(arg0: Map<K, C>, arg1: Function1<C, Ior<A, B>>):
    Tuple2<Kind<Kind<ForMapK, K>, A>, Kind<Kind<ForMapK, K>, B>> =
    arrow.core.extensions.map.unalign.Map
   .unalign<K>()
   .unalignWith<A, B, C>(arrow.core.MapK(arg0), arg1) as
    arrow.core.Tuple2<arrow.Kind<arrow.Kind<arrow.core.ForMapK, K>, A>,
    arrow.Kind<arrow.Kind<arrow.core.ForMapK, K>, B>>

/**
 * cached extension
 */
@PublishedApi()
internal val unalign_singleton: MapKUnalign<Any?> = object : MapKUnalign<Any?> {}

object Map {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated("Unalign typeclasses is deprecated. Use concrete methods on Map")
  inline fun <K> unalign(): MapKUnalign<K> = unalign_singleton as
      arrow.core.extensions.MapKUnalign<K>}
