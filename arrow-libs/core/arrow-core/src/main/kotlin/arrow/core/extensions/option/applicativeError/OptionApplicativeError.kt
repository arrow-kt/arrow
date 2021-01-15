package arrow.core.extensions.option.applicativeError

import arrow.Kind
import arrow.core.Either
import arrow.core.ForOption
import arrow.core.Option
import arrow.core.Option.Companion
import arrow.core.extensions.OptionApplicativeError
import arrow.typeclasses.ApplicativeError
import kotlin.Deprecated
import kotlin.Function0
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.Throwable
import kotlin.Unit
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val applicativeError_singleton: OptionApplicativeError = object :
    arrow.core.extensions.OptionApplicativeError {}

@JvmName("handleErrorWith")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "handleErrorWith(arg1)",
  "arrow.core.handleErrorWith"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForOption, A>.handleErrorWith(arg1: Function1<Unit, Kind<ForOption, A>>): Option<A> =
    arrow.core.Option.applicativeError().run {
  this@handleErrorWith.handleErrorWith<A>(arg1) as arrow.core.Option<A>
}

@JvmName("raiseError1")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "raiseError()",
  "arrow.core.raiseError"
  ),
  DeprecationLevel.WARNING
)
fun <A> Unit.raiseError(): Option<A> = arrow.core.Option.applicativeError().run {
  this@raiseError.raiseError<A>() as arrow.core.Option<A>
}

@JvmName("fromOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "fromOption(arg1)",
  "arrow.core.fromOption"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForOption, A>.fromOption(arg1: Function0<Unit>): Option<A> =
    arrow.core.Option.applicativeError().run {
  this@fromOption.fromOption<A>(arg1) as arrow.core.Option<A>
}

@JvmName("fromEither")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "fromEither(arg1)",
  "arrow.core.fromEither"
  ),
  DeprecationLevel.WARNING
)
fun <A, EE> Either<EE, A>.fromEither(arg1: Function1<EE, Unit>): Option<A> =
    arrow.core.Option.applicativeError().run {
  this@fromEither.fromEither<A, EE>(arg1) as arrow.core.Option<A>
}

@JvmName("handleError")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "handleError(arg1)",
  "arrow.core.handleError"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForOption, A>.handleError(arg1: Function1<Unit, A>): Option<A> =
    arrow.core.Option.applicativeError().run {
  this@handleError.handleError<A>(arg1) as arrow.core.Option<A>
}

@JvmName("redeem")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "redeem(arg1, arg2)",
  "arrow.core.redeem"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForOption, A>.redeem(arg1: Function1<Unit, B>, arg2: Function1<A, B>): Option<B> =
    arrow.core.Option.applicativeError().run {
  this@redeem.redeem<A, B>(arg1, arg2) as arrow.core.Option<B>
}

@JvmName("attempt")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "attempt()",
  "arrow.core.attempt"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForOption, A>.attempt(): Option<Either<Unit, A>> =
    arrow.core.Option.applicativeError().run {
  this@attempt.attempt<A>() as arrow.core.Option<arrow.core.Either<kotlin.Unit, A>>
}

@JvmName("catch")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "catch(arg0, arg1)",
  "arrow.core.Option.catch"
  ),
  DeprecationLevel.WARNING
)
fun <A> catch(arg0: Function1<Throwable, Unit>, arg1: Function0<A>): Option<A> = arrow.core.Option
   .applicativeError()
   .catch<A>(arg0, arg1) as arrow.core.Option<A>

@JvmName("catch")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "catch(arg1)",
  "arrow.core.catch"
  ),
  DeprecationLevel.WARNING
)
fun <A> ApplicativeError<ForOption, Throwable>.catch(arg1: Function0<A>): Option<A> =
    arrow.core.Option.applicativeError().run {
  this@catch.catch<A>(arg1) as arrow.core.Option<A>
}

@JvmName("effectCatch")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "effectCatch(arg0, arg1)",
  "arrow.core.Option.effectCatch"
  ),
  DeprecationLevel.WARNING
)
suspend fun <A> effectCatch(arg0: Function1<Throwable, Unit>, arg1: suspend () -> A): Option<A> =
    arrow.core.Option
   .applicativeError()
   .effectCatch<A>(arg0, arg1) as arrow.core.Option<A>

@JvmName("effectCatch")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "effectCatch(arg1)",
  "arrow.core.effectCatch"
  ),
  DeprecationLevel.WARNING
)
suspend fun <F, A> ApplicativeError<F, Throwable>.effectCatch(arg1: suspend () -> A): Kind<F, A> =
    arrow.core.Option.applicativeError().run {
  this@effectCatch.effectCatch<F, A>(arg1) as arrow.Kind<F, A>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun Companion.applicativeError(): OptionApplicativeError = applicativeError_singleton
