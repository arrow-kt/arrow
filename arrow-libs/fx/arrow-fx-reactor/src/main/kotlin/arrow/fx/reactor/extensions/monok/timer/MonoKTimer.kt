package arrow.fx.reactor.extensions.monok.timer

import arrow.fx.reactor.DeprecateReactor
import arrow.fx.reactor.MonoK.Companion
import arrow.fx.reactor.extensions.MonoKTimer
import kotlin.PublishedApi
import kotlin.Suppress

/**
 * cached extension
 */
@PublishedApi()
internal val timer_singleton: MonoKTimer = object : arrow.fx.reactor.extensions.MonoKTimer {}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateReactor)
inline fun Companion.timer(): MonoKTimer = timer_singleton
