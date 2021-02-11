package arrow.fx.reactor.extensions.monok.applicativeError

import arrow.Kind
import arrow.core.Either
import arrow.core.ForOption
import arrow.fx.reactor.DeprecateReactor
import arrow.fx.reactor.ForMonoK
import arrow.fx.reactor.MonoK
import arrow.fx.reactor.MonoK.Companion
import arrow.fx.reactor.extensions.MonoKApplicativeError
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
internal val applicativeError_singleton: MonoKApplicativeError = object :
    arrow.fx.reactor.extensions.MonoKApplicativeError {}

@JvmName("handleErrorWith")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A> Kind<ForMonoK, A>.handleErrorWith(arg1: Function1<Throwable, Kind<ForMonoK, A>>): MonoK<A> =
    arrow.fx.reactor.MonoK.applicativeError().run {
  this@handleErrorWith.handleErrorWith<A>(arg1) as arrow.fx.reactor.MonoK<A>
}

@JvmName("raiseError1")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A> Throwable.raiseError(): MonoK<A> = arrow.fx.reactor.MonoK.applicativeError().run {
  this@raiseError.raiseError<A>() as arrow.fx.reactor.MonoK<A>
}

@JvmName("fromOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A> Kind<ForOption, A>.fromOption(arg1: Function0<Throwable>): MonoK<A> =
    arrow.fx.reactor.MonoK.applicativeError().run {
  this@fromOption.fromOption<A>(arg1) as arrow.fx.reactor.MonoK<A>
}

@JvmName("fromEither")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A, EE> Either<EE, A>.fromEither(arg1: Function1<EE, Throwable>): MonoK<A> =
    arrow.fx.reactor.MonoK.applicativeError().run {
  this@fromEither.fromEither<A, EE>(arg1) as arrow.fx.reactor.MonoK<A>
}

@JvmName("handleError")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A> Kind<ForMonoK, A>.handleError(arg1: Function1<Throwable, A>): MonoK<A> =
    arrow.fx.reactor.MonoK.applicativeError().run {
  this@handleError.handleError<A>(arg1) as arrow.fx.reactor.MonoK<A>
}

@JvmName("redeem")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A, B> Kind<ForMonoK, A>.redeem(arg1: Function1<Throwable, B>, arg2: Function1<A, B>): MonoK<B> =
    arrow.fx.reactor.MonoK.applicativeError().run {
  this@redeem.redeem<A, B>(arg1, arg2) as arrow.fx.reactor.MonoK<B>
}

@JvmName("attempt")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A> Kind<ForMonoK, A>.attempt(): MonoK<Either<Throwable, A>> =
    arrow.fx.reactor.MonoK.applicativeError().run {
  this@attempt.attempt<A>() as arrow.fx.reactor.MonoK<arrow.core.Either<kotlin.Throwable, A>>
}

@JvmName("catch")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A> catch(arg0: Function1<Throwable, Throwable>, arg1: Function0<A>): MonoK<A> =
    arrow.fx.reactor.MonoK
   .applicativeError()
   .catch<A>(arg0, arg1) as arrow.fx.reactor.MonoK<A>

@JvmName("catch")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A> ApplicativeError<ForMonoK, Throwable>.catch(arg1: Function0<A>): MonoK<A> =
    arrow.fx.reactor.MonoK.applicativeError().run {
  this@catch.catch<A>(arg1) as arrow.fx.reactor.MonoK<A>
}

@JvmName("effectCatch")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
suspend fun <A> effectCatch(arg0: Function1<Throwable, Throwable>, arg1: suspend () -> A): MonoK<A> =
    arrow.fx.reactor.MonoK
   .applicativeError()
   .effectCatch<A>(arg0, arg1) as arrow.fx.reactor.MonoK<A>

@JvmName("effectCatch")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
suspend fun <F, A> ApplicativeError<F, Throwable>.effectCatch(arg1: suspend () -> A): Kind<F, A> =
    arrow.fx.reactor.MonoK.applicativeError().run {
  this@effectCatch.effectCatch<F, A>(arg1) as arrow.Kind<F, A>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateReactor)
inline fun Companion.applicativeError(): MonoKApplicativeError = applicativeError_singleton
