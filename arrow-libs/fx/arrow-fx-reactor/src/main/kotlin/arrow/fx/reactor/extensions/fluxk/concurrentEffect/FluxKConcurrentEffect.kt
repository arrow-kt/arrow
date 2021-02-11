package arrow.fx.reactor.extensions.fluxk.concurrentEffect

import arrow.Kind
import arrow.core.Either
import arrow.fx.reactor.DeprecateReactor
import arrow.fx.reactor.FluxK
import arrow.fx.reactor.FluxK.Companion
import arrow.fx.reactor.ForFluxK
import arrow.fx.reactor.extensions.FluxKConcurrentEffect
import kotlin.Deprecated
import kotlin.Function0
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
internal val concurrentEffect_singleton: FluxKConcurrentEffect = object :
    arrow.fx.reactor.extensions.FluxKConcurrentEffect {}

@JvmName("runAsyncCancellable")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A> Kind<ForFluxK, A>.runAsyncCancellable(
  arg1: Function1<Either<Throwable, A>, Kind<ForFluxK,
Unit>>
): FluxK<Function0<Unit>> = arrow.fx.reactor.FluxK.concurrentEffect().run {
  this@runAsyncCancellable.runAsyncCancellable<A>(arg1) as
    arrow.fx.reactor.FluxK<kotlin.Function0<kotlin.Unit>>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateReactor)
inline fun Companion.concurrentEffect(): FluxKConcurrentEffect = concurrentEffect_singleton
