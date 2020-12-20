package arrow.core.extensions.either.bifunctor

import arrow.Kind
import arrow.core.Either
import arrow.core.leftWiden as _leftWiden
import arrow.core.Either.Companion
import arrow.core.ForEither
import arrow.core.extensions.EitherBifunctor
import arrow.core.fix
import arrow.typeclasses.Conested
import arrow.typeclasses.Functor
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val bifunctor_singleton: EitherBifunctor = object : arrow.core.extensions.EitherBifunctor {}

@JvmName("bimap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("bimap(arg1, arg2)"))
fun <A, B, C, D> Kind<Kind<ForEither, A>, B>.bimap(arg1: Function1<A, C>, arg2: Function1<B, D>):
  Either<C, D> =
  fix().bimap(arg1, arg2)

@JvmName("lift")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Either.lift(arg0, arg1)"))
fun <A, B, C, D> lift(arg0: Function1<A, C>, arg1: Function1<B, D>): Function1<Kind<Kind<ForEither,
  A>, B>, Kind<Kind<ForEither, C>, D>> = arrow.core.Either
  .bifunctor()
  .lift<A, B, C, D>(arg0, arg1) as kotlin.Function1<arrow.Kind<arrow.Kind<arrow.core.ForEither, A>,
  B>, arrow.Kind<arrow.Kind<arrow.core.ForEither, C>, D>>

@JvmName("mapLeft")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("mapLeft(arg1)"))
fun <A, B, C> Kind<Kind<ForEither, A>, B>.mapLeft(arg1: Function1<A, C>): Either<C, B> =
  fix().mapLeft(arg1)

@JvmName("rightFunctor")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("Functor typeclasses is deprecated. Use concrete methods on Either")
fun <X> rightFunctor(): Functor<Kind<ForEither, X>> = arrow.core.Either
  .bifunctor()
  .rightFunctor<X>() as arrow.typeclasses.Functor<arrow.Kind<arrow.core.ForEither, X>>

@JvmName("leftFunctor")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("Functor typeclasses is deprecated. Use concrete methods on Either")
fun <X> leftFunctor(): Functor<Conested<ForEither, X>> = arrow.core.Either
  .bifunctor()
  .leftFunctor<X>() as arrow.typeclasses.Functor<arrow.typeclasses.Conested<arrow.core.ForEither,
  X>>

@JvmName("leftWiden")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <AA, B, A : AA> Kind<Kind<ForEither, A>, B>.leftWiden(): Either<AA, B> =
  fix()._leftWiden()

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("BiFunctor typeclasses is deprecated. Use concrete methods on Either")
inline fun Companion.bifunctor(): EitherBifunctor = bifunctor_singleton
