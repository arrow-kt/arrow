package arrow.fx.rx2.extensions.flowablek.monadError

import arrow.Kind
import arrow.core.Either
import arrow.fx.rx2.DeprecateRxJava
import arrow.fx.rx2.FlowableK
import arrow.fx.rx2.FlowableK.Companion
import arrow.fx.rx2.ForFlowableK
import arrow.fx.rx2.extensions.FlowableKMonadError
import kotlin.Boolean
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
internal val monadError_singleton: FlowableKMonadError = object :
  arrow.fx.rx2.extensions.FlowableKMonadError {}

@JvmName("ensure")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> Kind<ForFlowableK, A>.ensure(arg1: Function0<Throwable>, arg2: Function1<A, Boolean>):
  FlowableK<A> = arrow.fx.rx2.FlowableK.monadError().run {
    this@ensure.ensure<A>(arg1, arg2) as arrow.fx.rx2.FlowableK<A>
  }

@JvmName("redeemWith")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForFlowableK, A>.redeemWith(
  arg1: Function1<Throwable, Kind<ForFlowableK, B>>,
  arg2: Function1<A, Kind<ForFlowableK, B>>
): FlowableK<B> =
  arrow.fx.rx2.FlowableK.monadError().run {
    this@redeemWith.redeemWith<A, B>(arg1, arg2) as arrow.fx.rx2.FlowableK<B>
  }

@JvmName("rethrow")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> Kind<ForFlowableK, Either<Throwable, A>>.rethrow(): FlowableK<A> =
  arrow.fx.rx2.FlowableK.monadError().run {
    this@rethrow.rethrow<A>() as arrow.fx.rx2.FlowableK<A>
  }

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateRxJava)
inline fun Companion.monadError(): FlowableKMonadError = monadError_singleton
