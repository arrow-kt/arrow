package arrow.core.extensions.mapk.traverse

import arrow.Kind
import arrow.core.ForMapK
import arrow.core.MapK
import arrow.core.MapK.Companion
import arrow.core.extensions.MapKTraverse
import arrow.typeclasses.Applicative
import arrow.typeclasses.Monad
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
internal val traverse_singleton: MapKTraverse<Any?> = object : MapKTraverse<Any?> {}

@JvmName("traverse")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated. Replace with traverseEither or traverseValidated from arrow.core.*")
fun <K, G, A, B> Kind<Kind<ForMapK, K>, A>.traverse(
  arg1: Applicative<G>,
  arg2: Function1<A, Kind<G, B>>
): Kind<G, Kind<Kind<ForMapK, K>, B>> = arrow.core.MapK.traverse<K>().run {
  this@traverse.traverse<G, A, B>(arg1, arg2) as arrow.Kind<G,
    arrow.Kind<arrow.Kind<arrow.core.ForMapK, K>, B>>
}

@JvmName("sequence")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated. Replace with sequenceEither or sequenceValidated from arrow.core.*")
fun <K, G, A> Kind<Kind<ForMapK, K>, Kind<G, A>>.sequence(arg1: Applicative<G>): Kind<G,
    Kind<Kind<ForMapK, K>, A>> = arrow.core.MapK.traverse<K>().run {
  this@sequence.sequence<G, A>(arg1) as arrow.Kind<G, arrow.Kind<arrow.Kind<arrow.core.ForMapK, K>,
    A>>
}

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith("mapValues { (_, a) -> arg1(a) }"),
  DeprecationLevel.WARNING
)
fun <K, A, B> Kind<Kind<ForMapK, K>, A>.map(arg1: Function1<A, B>): MapK<K, B> =
    arrow.core.MapK.traverse<K>().run {
  this@map.map<A, B>(arg1) as arrow.core.MapK<K, B>
}

@JvmName("flatTraverse")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated. Replace with flatTraverseEither or flatTraverseValidated from arrow.core.*")
fun <K, G, A, B> Kind<Kind<ForMapK, K>, A>.flatTraverse(
  arg1: Monad<Kind<ForMapK, K>>,
  arg2: Applicative<G>,
  arg3: Function1<A, Kind<G, Kind<Kind<ForMapK, K>, B>>>
): Kind<G, Kind<Kind<ForMapK, K>, B>> = arrow.core.MapK.traverse<K>().run {
  this@flatTraverse.flatTraverse<G, A, B>(arg1, arg2, arg3) as arrow.Kind<G,
    arrow.Kind<arrow.Kind<arrow.core.ForMapK, K>, B>>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("Traverse typeclasses is deprecated. Use concrete methods on Map")
inline fun <K> Companion.traverse(): MapKTraverse<K> = traverse_singleton as
    arrow.core.extensions.MapKTraverse<K>
