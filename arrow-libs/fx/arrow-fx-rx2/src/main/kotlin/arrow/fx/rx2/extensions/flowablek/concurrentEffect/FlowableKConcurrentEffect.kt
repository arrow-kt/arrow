package arrow.fx.rx2.extensions.flowablek.concurrentEffect

import arrow.Kind
import arrow.core.Either
import arrow.fx.rx2.DeprecateRxJava
import arrow.fx.rx2.FlowableK
import arrow.fx.rx2.FlowableK.Companion
import arrow.fx.rx2.ForFlowableK
import arrow.fx.rx2.extensions.FlowableKConcurrentEffect
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
internal val concurrentEffect_singleton: FlowableKConcurrentEffect = object :
  arrow.fx.rx2.extensions.FlowableKConcurrentEffect {}

@JvmName("runAsyncCancellable")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> Kind<ForFlowableK, A>.runAsyncCancellable(
  arg1: Function1<Either<Throwable, A>,
    Kind<ForFlowableK, Unit>>
): FlowableK<Function0<Unit>> =
  arrow.fx.rx2.FlowableK.concurrentEffect().run {
    this@runAsyncCancellable.runAsyncCancellable<A>(arg1) as
      arrow.fx.rx2.FlowableK<kotlin.Function0<kotlin.Unit>>
  }

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateRxJava)
inline fun Companion.concurrentEffect(): FlowableKConcurrentEffect = concurrentEffect_singleton
