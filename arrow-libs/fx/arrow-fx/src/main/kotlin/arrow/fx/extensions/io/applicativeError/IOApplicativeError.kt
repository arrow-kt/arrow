package arrow.fx.extensions.io.applicativeError

import arrow.Kind
import arrow.core.Either
import arrow.core.ForOption
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.IO.Companion
import arrow.fx.IODeprecation
import arrow.fx.extensions.IOApplicativeError
import arrow.typeclasses.ApplicativeError
import kotlin.Deprecated
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
internal val applicativeError_singleton: IOApplicativeError = object :
    arrow.fx.extensions.IOApplicativeError {}

@JvmName("handleErrorWith")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A> Kind<ForIO, A>.handleErrorWith(arg1: Function1<Throwable, Kind<ForIO, A>>): IO<A> =
    arrow.fx.IO.applicativeError().run {
  this@handleErrorWith.handleErrorWith<A>(arg1) as arrow.fx.IO<A>
}

@JvmName("raiseError1")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A> Throwable.raiseError(): IO<A> = arrow.fx.IO.applicativeError().run {
  this@raiseError.raiseError<A>() as arrow.fx.IO<A>
}

@JvmName("fromOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A> Kind<ForOption, A>.fromOption(arg1: Function0<Throwable>): IO<A> =
    arrow.fx.IO.applicativeError().run {
  this@fromOption.fromOption<A>(arg1) as arrow.fx.IO<A>
}

@JvmName("fromEither")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, EE> Either<EE, A>.fromEither(arg1: Function1<EE, Throwable>): IO<A> =
    arrow.fx.IO.applicativeError().run {
  this@fromEither.fromEither<A, EE>(arg1) as arrow.fx.IO<A>
}

@JvmName("handleError")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A> Kind<ForIO, A>.handleError(arg1: Function1<Throwable, A>): IO<A> =
    arrow.fx.IO.applicativeError().run {
  this@handleError.handleError<A>(arg1) as arrow.fx.IO<A>
}

@JvmName("redeem")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B> Kind<ForIO, A>.redeem(arg1: Function1<Throwable, B>, arg2: Function1<A, B>): IO<B> =
    arrow.fx.IO.applicativeError().run {
  this@redeem.redeem<A, B>(arg1, arg2) as arrow.fx.IO<B>
}

@JvmName("attempt")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A> Kind<ForIO, A>.attempt(): IO<Either<Throwable, A>> = arrow.fx.IO.applicativeError().run {
  this@attempt.attempt<A>() as arrow.fx.IO<arrow.core.Either<kotlin.Throwable, A>>
}

@JvmName("catch")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A> catch(arg0: Function1<Throwable, Throwable>, arg1: Function0<A>): IO<A> = arrow.fx.IO
   .applicativeError()
   .catch<A>(arg0, arg1) as arrow.fx.IO<A>

@JvmName("catch")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A> ApplicativeError<ForIO, Throwable>.catch(arg1: Function0<A>): IO<A> =
    arrow.fx.IO.applicativeError().run {
  this@catch.catch<A>(arg1) as arrow.fx.IO<A>
}

@JvmName("effectCatch")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
suspend fun <A> effectCatch(arg0: Function1<Throwable, Throwable>, arg1: suspend () -> A): IO<A> =
    arrow.fx.IO
   .applicativeError()
   .effectCatch<A>(arg0, arg1) as arrow.fx.IO<A>

@JvmName("effectCatch")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
suspend fun <F, A> ApplicativeError<F, Throwable>.effectCatch(arg1: suspend () -> A): Kind<F, A> =
    arrow.fx.IO.applicativeError().run {
  this@effectCatch.effectCatch<F, A>(arg1) as arrow.Kind<F, A>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(IODeprecation)
inline fun Companion.applicativeError(): IOApplicativeError = applicativeError_singleton
