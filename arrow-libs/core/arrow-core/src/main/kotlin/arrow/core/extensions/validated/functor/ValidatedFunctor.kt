package arrow.core.extensions.validated.functor

import arrow.Kind
import arrow.core.ForValidated
import arrow.core.Tuple2
import arrow.core.Validated
import arrow.core.Validated.Companion
import arrow.core.extensions.ValidatedFunctor
import arrow.core.fix
import arrow.core.widen
import arrow.core.mapConst as _mapConst
import kotlin.Any
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.Unit
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val functor_singleton: ValidatedFunctor<Any?> = object : ValidatedFunctor<Any?> {}

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("map(arg1)"))
fun <E, A, B> Kind<Kind<ForValidated, E>, A>.map(arg1: Function1<A, B>): Validated<E, B> =
    arrow.core.Validated.functor<E>().run {
  this@map.map<A, B>(arg1) as arrow.core.Validated<E, B>
}

@JvmName("imap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("map(arg1)"))
fun <E, A, B> Kind<Kind<ForValidated, E>, A>.imap(arg1: Function1<A, B>, arg2: Function1<B, A>):
    Validated<E, B> = arrow.core.Validated.functor<E>().run {
  this@imap.imap<A, B>(arg1, arg2) as arrow.core.Validated<E, B>
}

@JvmName("lift")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Validated.lift(arg1)", "arrow.core.lift"))
fun <E, A, B> lift(arg0: Function1<A, B>): Function1<Kind<Kind<ForValidated, E>, A>,
    Kind<Kind<ForValidated, E>, B>> = arrow.core.Validated
   .functor<E>()
   .lift<A, B>(arg0) as kotlin.Function1<arrow.Kind<arrow.Kind<arrow.core.ForValidated, E>, A>,
    arrow.Kind<arrow.Kind<arrow.core.ForValidated, E>, B>>

@JvmName("void")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("void()"))
fun <E, A> Kind<Kind<ForValidated, E>, A>.void(): Validated<E, Unit> =
  fix().void()

@JvmName("fproduct")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("fproduct(arg1)"))
fun <E, A, B> Kind<Kind<ForValidated, E>, A>.fproduct(arg1: Function1<A, B>): Validated<E, Tuple2<A, B>> =
  fix().fproduct(arg1)

@JvmName("mapConst")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("mapConst(arg1)"))
fun <E, A, B> Kind<Kind<ForValidated, E>, A>.mapConst(arg1: B): Validated<E, B> =
  fix().mapConst(arg1)

@JvmName("mapConst")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("mapConst(arg1)", "arrow.core.mapConst"))
fun <E, A, B> A.mapConst(arg1: Kind<Kind<ForValidated, E>, B>): Validated<E, A> =
  _mapConst(arg1.fix())

@JvmName("tupleLeft")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("tupleLeft(arg1)"))
fun <E, A, B> Kind<Kind<ForValidated, E>, A>.tupleLeft(arg1: B): Validated<E, Tuple2<B, A>> =
  fix().tupleLeft(arg1)

@JvmName("tupleRight")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("tupleRight(arg1)"))
fun <E, A, B> Kind<Kind<ForValidated, E>, A>.tupleRight(arg1: B): Validated<E, Tuple2<A, B>> =
  fix().tupleRight(arg1)

@JvmName("widen")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("widen()", "arrow.core.widen"))
fun <E, B, A : B> Kind<Kind<ForValidated, E>, A>.widen(): Validated<E, B> =
  fix().widen<E, B, A>()

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("Functor typeclasses is deprecated. Use concrete methods on Validated")
inline fun <E> Companion.functor(): ValidatedFunctor<E> = functor_singleton as
    arrow.core.extensions.ValidatedFunctor<E>
