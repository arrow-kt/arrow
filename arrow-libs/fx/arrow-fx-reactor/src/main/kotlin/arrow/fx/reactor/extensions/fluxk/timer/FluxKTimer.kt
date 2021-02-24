package arrow.fx.reactor.extensions.fluxk.timer

import arrow.fx.reactor.DeprecateReactor
import arrow.fx.reactor.FluxK.Companion
import arrow.fx.reactor.extensions.FluxKTimer
import kotlin.PublishedApi
import kotlin.Suppress

/**
 * cached extension
 */
@PublishedApi()
internal val timer_singleton: FluxKTimer = object : arrow.fx.reactor.extensions.FluxKTimer {}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateReactor)
inline fun Companion.timer(): FluxKTimer = timer_singleton
