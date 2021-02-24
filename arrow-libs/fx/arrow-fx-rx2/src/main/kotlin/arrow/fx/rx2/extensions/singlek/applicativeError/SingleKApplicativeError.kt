package arrow.fx.rx2.extensions.singlek.applicativeError

import arrow.Kind
import arrow.core.Either
import arrow.core.ForOption
import arrow.fx.rx2.DeprecateRxJava
import arrow.fx.rx2.ForSingleK
import arrow.fx.rx2.SingleK
import arrow.fx.rx2.SingleK.Companion
import arrow.fx.rx2.extensions.SingleKApplicativeError
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
internal val applicativeError_singleton: SingleKApplicativeError = object :
  arrow.fx.rx2.extensions.SingleKApplicativeError {}

@JvmName("handleErrorWith")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> Kind<ForSingleK, A>.handleErrorWith(arg1: Function1<Throwable, Kind<ForSingleK, A>>):
  SingleK<A> = arrow.fx.rx2.SingleK.applicativeError().run {
    this@handleErrorWith.handleErrorWith<A>(arg1) as arrow.fx.rx2.SingleK<A>
  }

@JvmName("raiseError1")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> Throwable.raiseError(): SingleK<A> = arrow.fx.rx2.SingleK.applicativeError().run {
  this@raiseError.raiseError<A>() as arrow.fx.rx2.SingleK<A>
}

@JvmName("fromOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> Kind<ForOption, A>.fromOption(arg1: Function0<Throwable>): SingleK<A> =
  arrow.fx.rx2.SingleK.applicativeError().run {
    this@fromOption.fromOption<A>(arg1) as arrow.fx.rx2.SingleK<A>
  }

@JvmName("fromEither")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, EE> Either<EE, A>.fromEither(arg1: Function1<EE, Throwable>): SingleK<A> =
  arrow.fx.rx2.SingleK.applicativeError().run {
    this@fromEither.fromEither<A, EE>(arg1) as arrow.fx.rx2.SingleK<A>
  }

@JvmName("handleError")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> Kind<ForSingleK, A>.handleError(arg1: Function1<Throwable, A>): SingleK<A> =
  arrow.fx.rx2.SingleK.applicativeError().run {
    this@handleError.handleError<A>(arg1) as arrow.fx.rx2.SingleK<A>
  }

@JvmName("redeem")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForSingleK, A>.redeem(arg1: Function1<Throwable, B>, arg2: Function1<A, B>):
  SingleK<B> = arrow.fx.rx2.SingleK.applicativeError().run {
    this@redeem.redeem<A, B>(arg1, arg2) as arrow.fx.rx2.SingleK<B>
  }

@JvmName("attempt")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> Kind<ForSingleK, A>.attempt(): SingleK<Either<Throwable, A>> =
  arrow.fx.rx2.SingleK.applicativeError().run {
    this@attempt.attempt<A>() as arrow.fx.rx2.SingleK<arrow.core.Either<kotlin.Throwable, A>>
  }

@JvmName("catch")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> catch(arg0: Function1<Throwable, Throwable>, arg1: Function0<A>): SingleK<A> =
  arrow.fx.rx2.SingleK
    .applicativeError()
    .catch<A>(arg0, arg1) as arrow.fx.rx2.SingleK<A>

@JvmName("catch")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> ApplicativeError<ForSingleK, Throwable>.catch(arg1: Function0<A>): SingleK<A> =
  arrow.fx.rx2.SingleK.applicativeError().run {
    this@catch.catch<A>(arg1) as arrow.fx.rx2.SingleK<A>
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
  SingleK<A> = arrow.fx.rx2.SingleK
    .applicativeError()
    .effectCatch<A>(arg0, arg1) as arrow.fx.rx2.SingleK<A>

@JvmName("effectCatch")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
suspend fun <F, A> ApplicativeError<F, Throwable>.effectCatch(arg1: suspend () -> A): Kind<F, A> =
  arrow.fx.rx2.SingleK.applicativeError().run {
    this@effectCatch.effectCatch<F, A>(arg1) as arrow.Kind<F, A>
  }

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateRxJava)
inline fun Companion.applicativeError(): SingleKApplicativeError = applicativeError_singleton
