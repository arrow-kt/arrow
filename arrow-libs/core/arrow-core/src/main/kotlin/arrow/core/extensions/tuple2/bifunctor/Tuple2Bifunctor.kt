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
    "fl(this.a) toT fr(this.b)",
    "arrow.core.toT"
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
    "{ a: Tuple2<A, B> -> arg0(a.a) toT arg2(a.b) }",
    "arrow.core.Tuple2"
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
    "arg1(this.a) toT this.b",
    "arrow.core.toT"
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
@Deprecated("Functor typeclasses is deprecated. Use concrete methods on Pair")
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
@Deprecated("Functor typeclasses is deprecated. Use concrete methods on Pair")
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
  ReplaceWith("this"),
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
@Deprecated("BiFunctor typeclasses is deprecated. Use concrete methods on Pair")
inline fun Companion.bifunctor(): Tuple2Bifunctor = bifunctor_singleton
