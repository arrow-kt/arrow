package arrow.fx.reactor.extensions.fluxk.effect

import arrow.Kind
import arrow.core.Either
import arrow.fx.reactor.DeprecateReactor
import arrow.fx.reactor.FluxK
import arrow.fx.reactor.FluxK.Companion
import arrow.fx.reactor.ForFluxK
import arrow.fx.reactor.extensions.FluxKEffect
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
internal val effect_singleton: FluxKEffect = object : arrow.fx.reactor.extensions.FluxKEffect {}

@JvmName("runAsync")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A> Kind<ForFluxK, A>.runAsync(arg1: Function1<Either<Throwable, A>, Kind<ForFluxK, Unit>>):
    FluxK<Unit> = arrow.fx.reactor.FluxK.effect().run {
  this@runAsync.runAsync<A>(arg1) as arrow.fx.reactor.FluxK<kotlin.Unit>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateReactor)
inline fun Companion.effect(): FluxKEffect = effect_singleton
