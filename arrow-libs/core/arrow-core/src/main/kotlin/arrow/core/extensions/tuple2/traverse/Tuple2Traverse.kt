package arrow.core.extensions.tuple2.traverse

import arrow.Kind
import arrow.core.ForTuple2
import arrow.core.Tuple2
import arrow.core.Tuple2.Companion
import arrow.core.extensions.Tuple2Traverse
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
internal val traverse_singleton: Tuple2Traverse<Any?> = object : Tuple2Traverse<Any?> {}

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
fun <F, G, A, B> Kind<Kind<ForTuple2, F>, A>.traverse(
  arg1: Applicative<G>,
  arg2: Function1<A, Kind<G, B>>
): Kind<G, Kind<Kind<ForTuple2, F>, B>> = arrow.core.Tuple2.traverse<F>().run {
  this@traverse.traverse<G, A, B>(arg1, arg2) as arrow.Kind<G,
    arrow.Kind<arrow.Kind<arrow.core.ForTuple2, F>, B>>
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
fun <F, G, A> Kind<Kind<ForTuple2, F>, Kind<G, A>>.sequence(arg1: Applicative<G>): Kind<G,
    Kind<Kind<ForTuple2, F>, A>> = arrow.core.Tuple2.traverse<F>().run {
  this@sequence.sequence<G, A>(arg1) as arrow.Kind<G, arrow.Kind<arrow.Kind<arrow.core.ForTuple2,
    F>, A>>
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
fun <F, A, B> Kind<Kind<ForTuple2, F>, A>.map(arg1: Function1<A, B>): Tuple2<F, B> =
    arrow.core.Tuple2.traverse<F>().run {
  this@map.map<A, B>(arg1) as arrow.core.Tuple2<F, B>
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
fun <F, G, A, B> Kind<Kind<ForTuple2, F>, A>.flatTraverse(
  arg1: Monad<Kind<ForTuple2, F>>,
  arg2: Applicative<G>,
  arg3: Function1<A, Kind<G, Kind<Kind<ForTuple2, F>, B>>>
): Kind<G, Kind<Kind<ForTuple2, F>, B>> = arrow.core.Tuple2.traverse<F>().run {
  this@flatTraverse.flatTraverse<G, A, B>(arg1, arg2, arg3) as arrow.Kind<G,
    arrow.Kind<arrow.Kind<arrow.core.ForTuple2, F>, B>>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <F> Companion.traverse(): Tuple2Traverse<F> = traverse_singleton as
    arrow.core.extensions.Tuple2Traverse<F>
