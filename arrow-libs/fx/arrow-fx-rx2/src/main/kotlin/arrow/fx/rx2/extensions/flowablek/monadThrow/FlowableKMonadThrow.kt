package arrow.fx.rx2.extensions.flowablek.monadThrow

import arrow.fx.rx2.DeprecateRxJava
import arrow.fx.rx2.FlowableK
import arrow.fx.rx2.FlowableK.Companion
import arrow.fx.rx2.extensions.FlowableKMonadThrow
import kotlin.Deprecated
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.Throwable
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val monadThrow_singleton: FlowableKMonadThrow = object :
  arrow.fx.rx2.extensions.FlowableKMonadThrow {}

@JvmName("raiseNonFatal")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> Throwable.raiseNonFatal(): FlowableK<A> = arrow.fx.rx2.FlowableK.monadThrow().run {
  this@raiseNonFatal.raiseNonFatal<A>() as arrow.fx.rx2.FlowableK<A>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateRxJava)
inline fun Companion.monadThrow(): FlowableKMonadThrow = monadThrow_singleton
