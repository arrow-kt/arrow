package arrow.fx.rx2.extensions.singlek.timer

import arrow.fx.rx2.DeprecateRxJava
import arrow.fx.rx2.SingleK.Companion
import arrow.fx.rx2.extensions.SingleKTimer
import kotlin.PublishedApi
import kotlin.Suppress

/**
 * cached extension
 */
@PublishedApi()
internal val timer_singleton: SingleKTimer = object : arrow.fx.rx2.extensions.SingleKTimer {}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateRxJava)
inline fun Companion.timer(): SingleKTimer = timer_singleton
