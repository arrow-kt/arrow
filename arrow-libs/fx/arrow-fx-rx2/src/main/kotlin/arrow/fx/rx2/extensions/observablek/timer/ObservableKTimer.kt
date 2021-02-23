package arrow.fx.rx2.extensions.observablek.timer

import arrow.fx.rx2.DeprecateRxJava
import arrow.fx.rx2.ObservableK.Companion
import arrow.fx.rx2.extensions.ObservableKTimer
import kotlin.PublishedApi
import kotlin.Suppress

/**
 * cached extension
 */
@PublishedApi()
internal val timer_singleton: ObservableKTimer = object : arrow.fx.rx2.extensions.ObservableKTimer
{}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateRxJava)
inline fun Companion.timer(): ObservableKTimer = timer_singleton
