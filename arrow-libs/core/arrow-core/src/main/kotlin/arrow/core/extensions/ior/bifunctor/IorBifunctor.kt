package arrow.core.extensions.ior.bifunctor

import arrow.Kind
import arrow.core.ForIor
import arrow.core.Ior
import arrow.core.Ior.Companion
import arrow.core.extensions.IorBifunctor
import arrow.typeclasses.Conested
import arrow.typeclasses.Functor

/**
 * cached extension
 */
@PublishedApi()
internal val bifunctor_singleton: IorBifunctor = object : arrow.core.extensions.IorBifunctor {}

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
  "this.bimap(arg1, arg2)"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D> Kind<Kind<ForIor, A>, B>.bimap(arg1: Function1<A, C>, arg2: Function1<B, D>):
    Ior<C, D> = arrow.core.Ior.bifunctor().run {
  this@bimap.bimap<A, B, C, D>(arg1, arg2) as arrow.core.Ior<C, D>
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
  "Ior.lift(arg0, arg1)",
  "arrow.core.Ior"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D> lift(arg0: Function1<A, C>, arg1: Function1<B, D>): Function1<Kind<Kind<ForIor, A>,
    B>, Kind<Kind<ForIor, C>, D>> = arrow.core.Ior
   .bifunctor()
   .lift<A, B, C, D>(arg0, arg1) as kotlin.Function1<arrow.Kind<arrow.Kind<arrow.core.ForIor, A>,
    B>, arrow.Kind<arrow.Kind<arrow.core.ForIor, C>, D>>

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
  "this.mapLeft(arg1)"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C> Kind<Kind<ForIor, A>, B>.mapLeft(arg1: Function1<A, C>): Ior<C, B> =
    arrow.core.Ior.bifunctor().run {
  this@mapLeft.mapLeft<A, B, C>(arg1) as arrow.core.Ior<C, B>
}

@JvmName("rightFunctor")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "Functor typeclass is deprecated. Use concrete methods on Ior",
  level = DeprecationLevel.WARNING
)
fun <X> rightFunctor(): Functor<Kind<ForIor, X>> = arrow.core.Ior
   .bifunctor()
   .rightFunctor<X>() as arrow.typeclasses.Functor<arrow.Kind<arrow.core.ForIor, X>>

@JvmName("leftFunctor")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "Functor typeclass is deprecated. Use concrete methods on Ior",
  level = DeprecationLevel.WARNING
)
fun <X> leftFunctor(): Functor<Conested<ForIor, X>> = arrow.core.Ior
   .bifunctor()
   .leftFunctor<X>() as arrow.typeclasses.Functor<arrow.typeclasses.Conested<arrow.core.ForIor, X>>

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
  "this.leftWiden()",
  "arrow.core.leftWiden"
  ),
  DeprecationLevel.WARNING
)
fun <AA, B, A : AA> Kind<Kind<ForIor, A>, B>.leftWiden(): Ior<AA, B> =
    arrow.core.Ior.bifunctor().run {
  this@leftWiden.leftWiden<AA, B, A>() as arrow.core.Ior<AA, B>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "Bifunctor typeclass is deprecated. Use concrete methods on Ior",
  level = DeprecationLevel.WARNING
)
inline fun Companion.bifunctor(): IorBifunctor = bifunctor_singleton
