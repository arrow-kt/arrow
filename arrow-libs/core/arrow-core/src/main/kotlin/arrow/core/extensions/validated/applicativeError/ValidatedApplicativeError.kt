package arrow.core.extensions.validated.applicativeError

import arrow.Kind
import arrow.core.Either
import arrow.core.ForOption
import arrow.core.ForValidated
import arrow.core.Validated
import arrow.core.handleErrorWith as _handleErrorWith
import arrow.core.handleError as _handleError
import arrow.core.attempt as _attempt
import arrow.core.Validated.Companion
import arrow.core.extensions.ValidatedApplicativeError
import arrow.core.fix
import arrow.core.invalid
import arrow.core.redeem
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.Semigroup
import kotlin.Function0
import kotlin.Function1
import kotlin.Suppress
import kotlin.Throwable
import kotlin.jvm.JvmName

@JvmName("handleErrorWith")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("handleErrorWith(arg1)", "arrow.core.handleErrorWith"))
fun <E, A> Kind<Kind<ForValidated, E>, A>.handleErrorWith(SE: Semigroup<E>, arg1: Function1<E, Kind<Kind<ForValidated, E>, A>>): Validated<E, A> =
  fix()._handleErrorWith(arg1)

@JvmName("raiseError1")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("invalid()", "arrow.core.invalid"))
fun <E, A> E.raiseError(SE: Semigroup<E>): Validated<E, A> =
  invalid()

@JvmName("fromOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Validated.fromOption(this, arg1)", "arrow.core.fromOption"))
fun <E, A> Kind<ForOption, A>.fromOption(SE: Semigroup<E>, arg1: Function0<E>): Validated<E, A> =
  Validated.fromOption(fix(), arg1)

@JvmName("fromEither")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Validated.fromEither(this).mapLeft(arg1)", "arrow.core.fromEither"))
fun <E, A, EE> Either<EE, A>.fromEither(SE: Semigroup<E>, arg1: Function1<EE, E>): Validated<E, A> =
  Validated.fromEither(this).mapLeft(arg1)

@JvmName("handleError")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("handleError(arg1)", "arrow.core.handleError"))
fun <E, A> Kind<Kind<ForValidated, E>, A>.handleError(SE: Semigroup<E>, arg1: Function1<E, A>): Validated<E, A> =
  fix()._handleError(arg1)

@JvmName("redeem")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("redeem(arg1, arg2)", "arrow.core.redeem"))
fun <E, A, B> Kind<Kind<ForValidated, E>, A>.redeem(
  SE: Semigroup<E>,
  arg1: Function1<E, B>,
  arg2: Function1<A, B>
): Validated<E, B> =
  fix().redeem(arg1, arg2)

@JvmName("attempt")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("attempt()", "arrow.core.attempt"))
fun <E, A> Kind<Kind<ForValidated, E>, A>.attempt(SE: Semigroup<E>): Validated<E, Either<E, A>> =
  fix()._attempt()

@JvmName("catch")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
 @Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Validated.catch(arg0, arg1)", "arrow.core.catch"))
fun <E, A> catch(
  SE: Semigroup<E>,
  arg0: Function1<Throwable, E>,
  arg1: Function0<A>
): Validated<E, A> =
  Companion.catch(arg0, arg1)

@JvmName("catch")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("This methods is invalid for Validated. ApplicativeError<F, Throwable> is inconsistent in `F`")
fun <E, A> ApplicativeError<Kind<ForValidated, E>, Throwable>.catch(
  SE: Semigroup<E>,
  arg1: Function0<A>
): Validated<E, A> = arrow.core.Validated.applicativeError<E>(SE).run {
  this@catch.catch<A>(arg1) as arrow.core.Validated<E, A>
}

@JvmName("effectCatch")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Validated.catch(arg0) { arg1() }", "arrow.core.catch"))
suspend fun <E, A> effectCatch(
  SE: Semigroup<E>,
  arg0: Function1<Throwable, E>,
  arg1: suspend () -> A
): Validated<E, A> =
   Validated.catch(arg0) { arg1() }

@JvmName("effectCatch")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("This methods is invalid for Validated. ApplicativeError<F, Throwable> is inconsistent in `F`")
suspend fun <E, F, A> ApplicativeError<F, Throwable>.effectCatch(
  SE: Semigroup<E>,
  arg1: suspend () -> A
): Kind<F, A> = arrow.core.Validated.applicativeError<E>(SE).run {
  this@effectCatch.effectCatch<F, A>(arg1) as arrow.Kind<F, A>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("ApplicativeError typeclasses is deprecated. Use concrete methods on Validated")
inline fun <E> Companion.applicativeError(SE: Semigroup<E>): ValidatedApplicativeError<E> = object :
  arrow.core.extensions.ValidatedApplicativeError<E> {
  override fun SE():
    arrow.typeclasses.Semigroup<E> = SE
}
