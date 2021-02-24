package arrow.fx.reactor.extensions.fluxk.monadError

import arrow.Kind
import arrow.core.Either
import arrow.fx.reactor.DeprecateReactor
import arrow.fx.reactor.FluxK
import arrow.fx.reactor.FluxK.Companion
import arrow.fx.reactor.ForFluxK
import arrow.fx.reactor.extensions.FluxKMonadError
import kotlin.Boolean
import kotlin.Deprecated
import kotlin.Function0
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.Throwable
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val monadError_singleton: FluxKMonadError = object :
  arrow.fx.reactor.extensions.FluxKMonadError {}

@JvmName("ensure")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A> Kind<ForFluxK, A>.ensure(arg1: Function0<Throwable>, arg2: Function1<A, Boolean>): FluxK<A> =
  arrow.fx.reactor.FluxK.monadError().run {
    this@ensure.ensure<A>(arg1, arg2) as arrow.fx.reactor.FluxK<A>
  }

@JvmName("redeemWith")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A, B> Kind<ForFluxK, A>.redeemWith(
  arg1: Function1<Throwable, Kind<ForFluxK, B>>,
  arg2: Function1<A, Kind<ForFluxK, B>>
): FluxK<B> = arrow.fx.reactor.FluxK.monadError().run {
  this@redeemWith.redeemWith<A, B>(arg1, arg2) as arrow.fx.reactor.FluxK<B>
}

@JvmName("rethrow")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A> Kind<ForFluxK, Either<Throwable, A>>.rethrow(): FluxK<A> =
  arrow.fx.reactor.FluxK.monadError().run {
    this@rethrow.rethrow<A>() as arrow.fx.reactor.FluxK<A>
  }

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateReactor)
inline fun Companion.monadError(): FluxKMonadError = monadError_singleton
