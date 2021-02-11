package arrow.fx.rx2.extensions.observablek.applicativeError

import arrow.Kind
import arrow.core.Either
import arrow.core.ForOption
import arrow.fx.rx2.DeprecateRxJava
import arrow.fx.rx2.ForObservableK
import arrow.fx.rx2.ObservableK
import arrow.fx.rx2.ObservableK.Companion
import arrow.fx.rx2.extensions.ObservableKApplicativeError
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
internal val applicativeError_singleton: ObservableKApplicativeError = object :
    arrow.fx.rx2.extensions.ObservableKApplicativeError {}

@JvmName("handleErrorWith")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> Kind<ForObservableK, A>.handleErrorWith(
  arg1: Function1<Throwable, Kind<ForObservableK,
A>>
): ObservableK<A> = arrow.fx.rx2.ObservableK.applicativeError().run {
  this@handleErrorWith.handleErrorWith<A>(arg1) as arrow.fx.rx2.ObservableK<A>
}

@JvmName("raiseError1")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> Throwable.raiseError(): ObservableK<A> = arrow.fx.rx2.ObservableK.applicativeError().run {
  this@raiseError.raiseError<A>() as arrow.fx.rx2.ObservableK<A>
}

@JvmName("fromOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> Kind<ForOption, A>.fromOption(arg1: Function0<Throwable>): ObservableK<A> =
    arrow.fx.rx2.ObservableK.applicativeError().run {
  this@fromOption.fromOption<A>(arg1) as arrow.fx.rx2.ObservableK<A>
}

@JvmName("fromEither")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, EE> Either<EE, A>.fromEither(arg1: Function1<EE, Throwable>): ObservableK<A> =
    arrow.fx.rx2.ObservableK.applicativeError().run {
  this@fromEither.fromEither<A, EE>(arg1) as arrow.fx.rx2.ObservableK<A>
}

@JvmName("handleError")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> Kind<ForObservableK, A>.handleError(arg1: Function1<Throwable, A>): ObservableK<A> =
    arrow.fx.rx2.ObservableK.applicativeError().run {
  this@handleError.handleError<A>(arg1) as arrow.fx.rx2.ObservableK<A>
}

@JvmName("redeem")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForObservableK, A>.redeem(arg1: Function1<Throwable, B>, arg2: Function1<A, B>):
    ObservableK<B> = arrow.fx.rx2.ObservableK.applicativeError().run {
  this@redeem.redeem<A, B>(arg1, arg2) as arrow.fx.rx2.ObservableK<B>
}

@JvmName("attempt")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> Kind<ForObservableK, A>.attempt(): ObservableK<Either<Throwable, A>> =
    arrow.fx.rx2.ObservableK.applicativeError().run {
  this@attempt.attempt<A>() as arrow.fx.rx2.ObservableK<arrow.core.Either<kotlin.Throwable, A>>
}

@JvmName("catch")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> catch(arg0: Function1<Throwable, Throwable>, arg1: Function0<A>): ObservableK<A> =
    arrow.fx.rx2.ObservableK
   .applicativeError()
   .catch<A>(arg0, arg1) as arrow.fx.rx2.ObservableK<A>

@JvmName("catch")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> ApplicativeError<ForObservableK, Throwable>.catch(arg1: Function0<A>): ObservableK<A> =
    arrow.fx.rx2.ObservableK.applicativeError().run {
  this@catch.catch<A>(arg1) as arrow.fx.rx2.ObservableK<A>
}

@JvmName("effectCatch")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
suspend fun <A> effectCatch(arg0: Function1<Throwable, Throwable>, arg1: suspend () -> A):
    ObservableK<A> = arrow.fx.rx2.ObservableK
   .applicativeError()
   .effectCatch<A>(arg0, arg1) as arrow.fx.rx2.ObservableK<A>

@JvmName("effectCatch")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
suspend fun <F, A> ApplicativeError<F, Throwable>.effectCatch(arg1: suspend () -> A): Kind<F, A> =
    arrow.fx.rx2.ObservableK.applicativeError().run {
  this@effectCatch.effectCatch<F, A>(arg1) as arrow.Kind<F, A>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateRxJava)
inline fun Companion.applicativeError(): ObservableKApplicativeError = applicativeError_singleton
