package arrow.core.extensions.mapk.unalign

import arrow.Kind
import arrow.core.ForMapK
import arrow.core.Ior
import arrow.core.MapK.Companion
import arrow.core.Tuple2
import arrow.core.extensions.MapKUnalign
import kotlin.Any
import kotlin.Deprecated
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val unalign_singleton: MapKUnalign<Any?> = object : MapKUnalign<Any?> {}

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
fun <K, A, B> unalign(arg0: Kind<Kind<ForMapK, K>, Ior<A, B>>): Tuple2<Kind<Kind<ForMapK, K>, A>,
    Kind<Kind<ForMapK, K>, B>> = arrow.core.MapK
   .unalign<K>()
   .unalign<A, B>(arg0) as arrow.core.Tuple2<arrow.Kind<arrow.Kind<arrow.core.ForMapK, K>, A>,
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
fun <K, A, B, C> unalignWith(arg0: Kind<Kind<ForMapK, K>, C>, arg1: Function1<C, Ior<A, B>>):
    Tuple2<Kind<Kind<ForMapK, K>, A>, Kind<Kind<ForMapK, K>, B>> = arrow.core.MapK
   .unalign<K>()
   .unalignWith<A, B, C>(arg0, arg1) as arrow.core.Tuple2<arrow.Kind<arrow.Kind<arrow.core.ForMapK,
    K>, A>, arrow.Kind<arrow.Kind<arrow.core.ForMapK, K>, B>>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("Unalign typeclasses is deprecated. Use concrete methods on Map")
inline fun <K> Companion.unalign(): MapKUnalign<K> = unalign_singleton as
    arrow.core.extensions.MapKUnalign<K>
