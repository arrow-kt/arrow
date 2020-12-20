package arrow.core.extensions.either.applicativeError

import arrow.Kind
import arrow.core.Either
import arrow.core.handleErrorWith as _handleErrorWith
import arrow.core.handleError as _handleError
import arrow.core.redeem as _redeem
import arrow.core.Either.Companion
import arrow.core.ForEither
import arrow.core.ForOption
import arrow.core.Left
import arrow.core.Right
import arrow.core.extensions.EitherApplicativeError
import arrow.core.fix
import arrow.core.left
import arrow.core.right
import arrow.typeclasses.ApplicativeError
import kotlin.Any
import kotlin.Function0
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.Throwable
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val applicativeError_singleton: EitherApplicativeError<Any?> = object :
  EitherApplicativeError<Any?> {}

@JvmName("handleErrorWith")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("handleErrorWith(arg1)", "arrow.core.handleErrorWith"))
fun <L, A> Kind<Kind<ForEither, L>, A>.handleErrorWith(
  arg1: Function1<L, Kind<Kind<ForEither, L>,
    A>>
): Either<L, A> =
  fix()._handleErrorWith(arg1)

@JvmName("raiseError1")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("this.left()", "arrow.core.left"))
fun <L, A> L.raiseError(): Either<L, A> =
  left()

@JvmName("fromOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Either.fromNullable(fix().orNull()).mapLeft { arg1.invoke() }", "arrow.core.fromNullable"))
fun <L, A> Kind<ForOption, A>.fromOption(arg1: Function0<L>): Either<L, A> =
  Either.fromNullable(fix().orNull()).mapLeft { arg1.invoke() }

@JvmName("fromEither")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("mapLeft(arg1)"))
fun <L, A, EE> Either<EE, A>.fromEither(arg1: Function1<EE, L>): Either<L, A> =
  mapLeft(arg1)

@JvmName("handleError")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("handleError(arg1)"))
fun <L, A> Kind<Kind<ForEither, L>, A>.handleError(arg1: Function1<L, A>): Either<L, A> =
  fix()._handleError(arg1)

@JvmName("redeem")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("redeem(arg1, arg2)"))
fun <L, A, B> Kind<Kind<ForEither, L>, A>.redeem(arg1: Function1<L, B>, arg2: Function1<A, B>): Either<L, B> =
  fix()._redeem(arg1, arg2)

@JvmName("attempt")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("map { it.right() }.handleErrorWith { it.left().right() }", "arrow.core.right", "arrow.core.left", "arrow.core.handleErrorWith"))
fun <L, A> Kind<Kind<ForEither, L>, A>.attempt(): Either<L, Either<L, A>> =
  fix().map { Right(it) }.handleErrorWith { Left(it).right() }

@JvmName("catch")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Either.catch(arg0, arg1)", "arrow.core.catch"))
fun <L, A> catch(arg0: Function1<Throwable, L>, arg1: Function0<A>): Either<L, A> =
  Either.catch(arg0, arg1)

@JvmName("catch")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("This methods is invalid for Validated. ApplicativeError<F, Throwable> is inconsistent in `F`")
fun <L, A> ApplicativeError<Kind<ForEither, L>, Throwable>.catch(arg1: Function0<A>): Either<L, A> =
  arrow.core.Either.applicativeError<L>().run {
    catch(arg1) as arrow.core.Either<L, A>
  }

@JvmName("effectCatch")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Either.catch(arg0) { arg1() }", "arrow.core.catch"))
suspend fun <L, A> effectCatch(arg0: Function1<Throwable, L>, arg1: suspend () -> A): Either<L, A> =
  Either.catch(arg0) { arg1() }

@JvmName("effectCatch")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("This methods is invalid for Validated. ApplicativeError<F, Throwable> is inconsistent in `F`")
suspend fun <L, F, A> ApplicativeError<F, Throwable>.effectCatch(arg1: suspend () -> A): Kind<F, A> =
  arrow.core.Either.applicativeError<L>().run {
    this@effectCatch.effectCatch<F, A>(arg1) as arrow.Kind<F, A>
  }

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("ApplicativeError typeclasses is deprecated. Use concrete methods on Either")
inline fun <L> Companion.applicativeError(): EitherApplicativeError<L> = applicativeError_singleton
  as arrow.core.extensions.EitherApplicativeError<L>
