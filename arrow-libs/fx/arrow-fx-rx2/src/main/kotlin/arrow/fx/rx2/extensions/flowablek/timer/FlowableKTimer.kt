package arrow.fx.rx2.extensions.flowablek.timer

import arrow.fx.rx2.DeprecateRxJava
import arrow.fx.rx2.FlowableK.Companion
import arrow.fx.rx2.extensions.FlowableKTimer
import kotlin.PublishedApi
import kotlin.Suppress

/**
 * cached extension
 */
@PublishedApi()
internal val timer_singleton: FlowableKTimer = object : arrow.fx.rx2.extensions.FlowableKTimer {}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateRxJava)
inline fun Companion.timer(): FlowableKTimer = timer_singleton
