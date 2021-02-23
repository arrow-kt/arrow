package arrow.fx.reactor.extensions.fluxk.applicative

import arrow.Kind
import arrow.fx.reactor.DeprecateReactor
import arrow.fx.reactor.FluxK
import arrow.fx.reactor.FluxK.Companion
import arrow.fx.reactor.ForFluxK
import arrow.fx.reactor.extensions.FluxKApplicative
import arrow.typeclasses.Monoid
import kotlin.Deprecated
import kotlin.Function1
import kotlin.Int
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val applicative_singleton: FluxKApplicative = object :
  arrow.fx.reactor.extensions.FluxKApplicative {}

@JvmName("just1")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A> A.just(): FluxK<A> = arrow.fx.reactor.FluxK.applicative().run {
  this@just.just<A>() as arrow.fx.reactor.FluxK<A>
}

@JvmName("unit")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun unit(): FluxK<Unit> = arrow.fx.reactor.FluxK
  .applicative()
  .unit() as arrow.fx.reactor.FluxK<kotlin.Unit>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A, B> Kind<ForFluxK, A>.map(arg1: Function1<A, B>): FluxK<B> =
  arrow.fx.reactor.FluxK.applicative().run {
    this@map.map<A, B>(arg1) as arrow.fx.reactor.FluxK<B>
  }

@JvmName("replicate")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A> Kind<ForFluxK, A>.replicate(arg1: Int): FluxK<List<A>> =
  arrow.fx.reactor.FluxK.applicative().run {
    this@replicate.replicate<A>(arg1) as arrow.fx.reactor.FluxK<kotlin.collections.List<A>>
  }

@JvmName("replicate")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A> Kind<ForFluxK, A>.replicate(arg1: Int, arg2: Monoid<A>): FluxK<A> =
  arrow.fx.reactor.FluxK.applicative().run {
    this@replicate.replicate<A>(arg1, arg2) as arrow.fx.reactor.FluxK<A>
  }

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateReactor)
inline fun Companion.applicative(): FluxKApplicative = applicative_singleton
