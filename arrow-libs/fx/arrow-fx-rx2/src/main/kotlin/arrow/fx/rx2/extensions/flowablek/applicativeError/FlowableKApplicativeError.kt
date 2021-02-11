package arrow.fx.rx2.extensions.flowablek.applicativeError

import arrow.Kind
import arrow.core.Either
import arrow.core.ForOption
import arrow.fx.rx2.DeprecateRxJava
import arrow.fx.rx2.FlowableK
import arrow.fx.rx2.FlowableK.Companion
import arrow.fx.rx2.ForFlowableK
import arrow.fx.rx2.extensions.FlowableKApplicativeError
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
internal val applicativeError_singleton: FlowableKApplicativeError = object :
    arrow.fx.rx2.extensions.FlowableKApplicativeError {}

@JvmName("handleErrorWith")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> Kind<ForFlowableK, A>.handleErrorWith(arg1: Function1<Throwable, Kind<ForFlowableK, A>>):
    FlowableK<A> = arrow.fx.rx2.FlowableK.applicativeError().run {
  this@handleErrorWith.handleErrorWith<A>(arg1) as arrow.fx.rx2.FlowableK<A>
}

@JvmName("raiseError1")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> Throwable.raiseError(): FlowableK<A> = arrow.fx.rx2.FlowableK.applicativeError().run {
  this@raiseError.raiseError<A>() as arrow.fx.rx2.FlowableK<A>
}

@JvmName("fromOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> Kind<ForOption, A>.fromOption(arg1: Function0<Throwable>): FlowableK<A> =
    arrow.fx.rx2.FlowableK.applicativeError().run {
  this@fromOption.fromOption<A>(arg1) as arrow.fx.rx2.FlowableK<A>
}

@JvmName("fromEither")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, EE> Either<EE, A>.fromEither(arg1: Function1<EE, Throwable>): FlowableK<A> =
    arrow.fx.rx2.FlowableK.applicativeError().run {
  this@fromEither.fromEither<A, EE>(arg1) as arrow.fx.rx2.FlowableK<A>
}

@JvmName("handleError")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> Kind<ForFlowableK, A>.handleError(arg1: Function1<Throwable, A>): FlowableK<A> =
    arrow.fx.rx2.FlowableK.applicativeError().run {
  this@handleError.handleError<A>(arg1) as arrow.fx.rx2.FlowableK<A>
}

@JvmName("redeem")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForFlowableK, A>.redeem(arg1: Function1<Throwable, B>, arg2: Function1<A, B>):
    FlowableK<B> = arrow.fx.rx2.FlowableK.applicativeError().run {
  this@redeem.redeem<A, B>(arg1, arg2) as arrow.fx.rx2.FlowableK<B>
}

@JvmName("attempt")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> Kind<ForFlowableK, A>.attempt(): FlowableK<Either<Throwable, A>> =
    arrow.fx.rx2.FlowableK.applicativeError().run {
  this@attempt.attempt<A>() as arrow.fx.rx2.FlowableK<arrow.core.Either<kotlin.Throwable, A>>
}

@JvmName("catch")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> catch(arg0: Function1<Throwable, Throwable>, arg1: Function0<A>): FlowableK<A> =
    arrow.fx.rx2.FlowableK
   .applicativeError()
   .catch<A>(arg0, arg1) as arrow.fx.rx2.FlowableK<A>

@JvmName("catch")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> ApplicativeError<ForFlowableK, Throwable>.catch(arg1: Function0<A>): FlowableK<A> =
    arrow.fx.rx2.FlowableK.applicativeError().run {
  this@catch.catch<A>(arg1) as arrow.fx.rx2.FlowableK<A>
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
    FlowableK<A> = arrow.fx.rx2.FlowableK
   .applicativeError()
   .effectCatch<A>(arg0, arg1) as arrow.fx.rx2.FlowableK<A>

@JvmName("effectCatch")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
suspend fun <F, A> ApplicativeError<F, Throwable>.effectCatch(arg1: suspend () -> A): Kind<F, A> =
    arrow.fx.rx2.FlowableK.applicativeError().run {
  this@effectCatch.effectCatch<F, A>(arg1) as arrow.Kind<F, A>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateRxJava)
inline fun Companion.applicativeError(): FlowableKApplicativeError = applicativeError_singleton
