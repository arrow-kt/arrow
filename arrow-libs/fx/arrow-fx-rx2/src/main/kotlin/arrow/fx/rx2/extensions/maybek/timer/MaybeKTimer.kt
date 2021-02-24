package arrow.fx.rx2.extensions.maybek.timer

import arrow.fx.rx2.DeprecateRxJava
import arrow.fx.rx2.MaybeK.Companion
import arrow.fx.rx2.extensions.MaybeKTimer
import kotlin.PublishedApi
import kotlin.Suppress

/**
 * cached extension
 */
@PublishedApi()
internal val timer_singleton: MaybeKTimer = object : arrow.fx.rx2.extensions.MaybeKTimer {}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateRxJava)
inline fun Companion.timer(): MaybeKTimer = timer_singleton
