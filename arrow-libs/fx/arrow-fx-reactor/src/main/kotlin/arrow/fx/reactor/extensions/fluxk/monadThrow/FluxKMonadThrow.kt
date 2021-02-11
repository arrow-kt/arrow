package arrow.fx.reactor.extensions.fluxk.monadThrow

import arrow.fx.reactor.DeprecateReactor
import arrow.fx.reactor.FluxK
import arrow.fx.reactor.FluxK.Companion
import arrow.fx.reactor.extensions.FluxKMonadThrow
import kotlin.Deprecated
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.Throwable
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val monadThrow_singleton: FluxKMonadThrow = object :
    arrow.fx.reactor.extensions.FluxKMonadThrow {}

@JvmName("raiseNonFatal")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A> Throwable.raiseNonFatal(): FluxK<A> = arrow.fx.reactor.FluxK.monadThrow().run {
  this@raiseNonFatal.raiseNonFatal<A>() as arrow.fx.reactor.FluxK<A>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateReactor)
inline fun Companion.monadThrow(): FluxKMonadThrow = monadThrow_singleton
