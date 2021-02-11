package arrow.fx.rx2.extensions.flowablek.effect

import arrow.Kind
import arrow.core.Either
import arrow.fx.rx2.DeprecateRxJava
import arrow.fx.rx2.FlowableK
import arrow.fx.rx2.FlowableK.Companion
import arrow.fx.rx2.ForFlowableK
import arrow.fx.rx2.extensions.FlowableKEffect
import kotlin.Deprecated
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
internal val effect_singleton: FlowableKEffect = object : arrow.fx.rx2.extensions.FlowableKEffect {}

@JvmName("runAsync")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> Kind<ForFlowableK, A>.runAsync(
  arg1: Function1<Either<Throwable, A>, Kind<ForFlowableK,
Unit>>
): FlowableK<Unit> = arrow.fx.rx2.FlowableK.effect().run {
  this@runAsync.runAsync<A>(arg1) as arrow.fx.rx2.FlowableK<kotlin.Unit>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateRxJava)
inline fun Companion.effect(): FlowableKEffect = effect_singleton
