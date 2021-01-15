package arrow.core.extensions.sequencek.traverse

import arrow.Kind
import arrow.core.ForSequenceK
import arrow.core.SequenceK
import arrow.core.SequenceK.Companion
import arrow.core.extensions.SequenceKTraverse
import arrow.typeclasses.Applicative
import arrow.typeclasses.Monad
import kotlin.Deprecated
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val traverse_singleton: SequenceKTraverse = object :
    arrow.core.extensions.SequenceKTraverse {}

@JvmName("traverse")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "traverse(arg1, arg2)",
  "arrow.core.traverse"
  ),
  DeprecationLevel.WARNING
)
fun <G, A, B> Kind<ForSequenceK, A>.traverse(arg1: Applicative<G>, arg2: Function1<A, Kind<G, B>>):
    Kind<G, Kind<ForSequenceK, B>> = arrow.core.SequenceK.traverse().run {
  this@traverse.traverse<G, A, B>(arg1, arg2) as arrow.Kind<G, arrow.Kind<arrow.core.ForSequenceK,
    B>>
}

@JvmName("sequence")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "sequence(arg1)",
  "arrow.core.sequence"
  ),
  DeprecationLevel.WARNING
)
fun <G, A> Kind<ForSequenceK, Kind<G, A>>.sequence(arg1: Applicative<G>): Kind<G, Kind<ForSequenceK,
    A>> = arrow.core.SequenceK.traverse().run {
  this@sequence.sequence<G, A>(arg1) as arrow.Kind<G, arrow.Kind<arrow.core.ForSequenceK, A>>
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
  ReplaceWith(
  "map(arg1)",
  "arrow.core.map"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForSequenceK, A>.map(arg1: Function1<A, B>): SequenceK<B> =
    arrow.core.SequenceK.traverse().run {
  this@map.map<A, B>(arg1) as arrow.core.SequenceK<B>
}

@JvmName("flatTraverse")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "flatTraverse(arg1, arg2, arg3)",
  "arrow.core.flatTraverse"
  ),
  DeprecationLevel.WARNING
)
fun <G, A, B> Kind<ForSequenceK, A>.flatTraverse(
  arg1: Monad<ForSequenceK>,
  arg2: Applicative<G>,
  arg3: Function1<A, Kind<G, Kind<ForSequenceK, B>>>
): Kind<G, Kind<ForSequenceK, B>> = arrow.core.SequenceK.traverse().run {
  this@flatTraverse.flatTraverse<G, A, B>(arg1, arg2, arg3) as arrow.Kind<G,
    arrow.Kind<arrow.core.ForSequenceK, B>>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun Companion.traverse(): SequenceKTraverse = traverse_singleton
