package arrow.core.extensions.validated.bifunctor

import arrow.Kind
import arrow.core.ForValidated
import arrow.core.Validated
import arrow.core.Validated.Companion
import arrow.core.extensions.ValidatedBifunctor
import arrow.core.fix
import arrow.core.leftWiden
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
internal val bifunctor_singleton: ValidatedBifunctor = object :
  arrow.core.extensions.ValidatedBifunctor {}

@JvmName("bimap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("bimap(arg1, arg2)"))
fun <A, B, C, D> Kind<Kind<ForValidated, A>, B>.bimap(arg1: Function1<A, C>, arg2: Function1<B, D>): Validated<C, D> =
  fix().bimap(arg1, arg2)

@JvmName("lift")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Validated.lift(arg0, arg1)", "arrow.core.lift"))
fun <A, B, C, D> lift(arg0: Function1<A, C>, arg1: Function1<B, D>): Function1<Kind<Kind<ForValidated, A>, B>, Kind<Kind<ForValidated, C>, D>> = arrow.core.Validated
  .bifunctor()
  .lift<A, B, C, D>(arg0, arg1) as kotlin.Function1<arrow.Kind<arrow.Kind<arrow.core.ForValidated,
  A>, B>, arrow.Kind<arrow.Kind<arrow.core.ForValidated, C>, D>>

@JvmName("mapLeft")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("mapLeft(arg1)", "arrow.core.mapLeft"))
fun <A, B, C> Kind<Kind<ForValidated, A>, B>.mapLeft(arg1: Function1<A, C>): Validated<C, B> =
  fix().mapLeft(arg1)

@JvmName("rightFunctor")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("Functor typeclasses is deprecated. Use concrete methods on Validated")
fun <X> rightFunctor(): Functor<Kind<ForValidated, X>> = arrow.core.Validated
  .bifunctor()
  .rightFunctor<X>() as arrow.typeclasses.Functor<arrow.Kind<arrow.core.ForValidated, X>>

@JvmName("leftFunctor")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("Functor typeclasses is deprecated. Use concrete methods on Validated")
fun <X> leftFunctor(): Functor<Conested<ForValidated, X>> = arrow.core.Validated
  .bifunctor()
  .leftFunctor<X>() as
  arrow.typeclasses.Functor<arrow.typeclasses.Conested<arrow.core.ForValidated, X>>

@JvmName("leftWiden")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("leftWiden()", "arrow.core.leftWiden"))
fun <AA, B, A : AA> Kind<Kind<ForValidated, A>, B>.leftWiden(): Validated<AA, B> =
  fix().leftWiden()

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("Functor typeclasses is deprecated. Use concrete methods on Validated")
inline fun Companion.bifunctor(): ValidatedBifunctor = bifunctor_singleton
