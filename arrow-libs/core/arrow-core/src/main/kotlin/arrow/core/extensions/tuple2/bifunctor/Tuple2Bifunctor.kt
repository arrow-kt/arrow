package arrow.core.extensions.tuple2.bifunctor

import arrow.Kind
import arrow.core.ForTuple2
import arrow.core.Tuple2
import arrow.core.Tuple2.Companion
import arrow.core.extensions.Tuple2Bifunctor
import arrow.typeclasses.Conested
import arrow.typeclasses.Functor
import kotlin.Deprecated
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val bifunctor_singleton: Tuple2Bifunctor = object : arrow.core.extensions.Tuple2Bifunctor
    {}

@JvmName("bimap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "bimap(arg1, arg2)",
  "arrow.core.bimap"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D> Kind<Kind<ForTuple2, A>, B>.bimap(arg1: Function1<A, C>, arg2: Function1<B, D>):
    Tuple2<C, D> = arrow.core.Tuple2.bifunctor().run {
  this@bimap.bimap<A, B, C, D>(arg1, arg2) as arrow.core.Tuple2<C, D>
}

@JvmName("lift")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "lift(arg0, arg1)",
  "arrow.core.Tuple2.lift"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D> lift(arg0: Function1<A, C>, arg1: Function1<B, D>): Function1<Kind<Kind<ForTuple2,
    A>, B>, Kind<Kind<ForTuple2, C>, D>> = arrow.core.Tuple2
   .bifunctor()
   .lift<A, B, C, D>(arg0, arg1) as kotlin.Function1<arrow.Kind<arrow.Kind<arrow.core.ForTuple2, A>,
    B>, arrow.Kind<arrow.Kind<arrow.core.ForTuple2, C>, D>>

@JvmName("mapLeft")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "mapLeft(arg1)",
  "arrow.core.mapLeft"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C> Kind<Kind<ForTuple2, A>, B>.mapLeft(arg1: Function1<A, C>): Tuple2<C, B> =
    arrow.core.Tuple2.bifunctor().run {
  this@mapLeft.mapLeft<A, B, C>(arg1) as arrow.core.Tuple2<C, B>
}

@JvmName("rightFunctor")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "rightFunctor()",
  "arrow.core.Tuple2.rightFunctor"
  ),
  DeprecationLevel.WARNING
)
fun <X> rightFunctor(): Functor<Kind<ForTuple2, X>> = arrow.core.Tuple2
   .bifunctor()
   .rightFunctor<X>() as arrow.typeclasses.Functor<arrow.Kind<arrow.core.ForTuple2, X>>

@JvmName("leftFunctor")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "leftFunctor()",
  "arrow.core.Tuple2.leftFunctor"
  ),
  DeprecationLevel.WARNING
)
fun <X> leftFunctor(): Functor<Conested<ForTuple2, X>> = arrow.core.Tuple2
   .bifunctor()
   .leftFunctor<X>() as arrow.typeclasses.Functor<arrow.typeclasses.Conested<arrow.core.ForTuple2,
    X>>

@JvmName("leftWiden")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "leftWiden()",
  "arrow.core.leftWiden"
  ),
  DeprecationLevel.WARNING
)
fun <AA, B, A : AA> Kind<Kind<ForTuple2, A>, B>.leftWiden(): Tuple2<AA, B> =
    arrow.core.Tuple2.bifunctor().run {
  this@leftWiden.leftWiden<AA, B, A>() as arrow.core.Tuple2<AA, B>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun Companion.bifunctor(): Tuple2Bifunctor = bifunctor_singleton
