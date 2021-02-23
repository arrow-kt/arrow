package arrow.fx.reactor.extensions.fluxk.monadFilter

import arrow.Kind
import arrow.core.Option
import arrow.fx.reactor.DeprecateReactor
import arrow.fx.reactor.FluxK
import arrow.fx.reactor.FluxK.Companion
import arrow.fx.reactor.ForFluxK
import arrow.fx.reactor.extensions.FluxKMonadFilter
import arrow.typeclasses.MonadFilterSyntax
import kotlin.Deprecated
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val monadFilter_singleton: FluxKMonadFilter = object :
  arrow.fx.reactor.extensions.FluxKMonadFilter {}

@JvmName("filterMap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A, B> Kind<ForFluxK, A>.filterMap(arg1: Function1<A, Option<B>>): FluxK<B> =
  arrow.fx.reactor.FluxK.monadFilter().run {
    this@filterMap.filterMap<A, B>(arg1) as arrow.fx.reactor.FluxK<B>
  }

@JvmName("bindingFilter")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <B> bindingFilter(arg0: suspend MonadFilterSyntax<ForFluxK>.() -> B): FluxK<B> =
  arrow.fx.reactor.FluxK
    .monadFilter()
    .bindingFilter<B>(arg0) as arrow.fx.reactor.FluxK<B>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateReactor)
inline fun Companion.monadFilter(): FluxKMonadFilter = monadFilter_singleton
