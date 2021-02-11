package arrow.fx.reactor.extensions.fluxk.functorFilter

import arrow.Kind
import arrow.core.Option
import arrow.fx.reactor.DeprecateReactor
import arrow.fx.reactor.FluxK
import arrow.fx.reactor.FluxK.Companion
import arrow.fx.reactor.ForFluxK
import arrow.fx.reactor.extensions.FluxKFunctorFilter
import java.lang.Class
import kotlin.Boolean
import kotlin.Deprecated
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val functorFilter_singleton: FluxKFunctorFilter = object :
    arrow.fx.reactor.extensions.FluxKFunctorFilter {}

@JvmName("filterMap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A, B> Kind<ForFluxK, A>.filterMap(arg1: Function1<A, Option<B>>): FluxK<B> =
    arrow.fx.reactor.FluxK.functorFilter().run {
  this@filterMap.filterMap<A, B>(arg1) as arrow.fx.reactor.FluxK<B>
}

@JvmName("flattenOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A> Kind<ForFluxK, Option<A>>.flattenOption(): FluxK<A> =
    arrow.fx.reactor.FluxK.functorFilter().run {
  this@flattenOption.flattenOption<A>() as arrow.fx.reactor.FluxK<A>
}

@JvmName("filter")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A> Kind<ForFluxK, A>.filter(arg1: Function1<A, Boolean>): FluxK<A> =
    arrow.fx.reactor.FluxK.functorFilter().run {
  this@filter.filter<A>(arg1) as arrow.fx.reactor.FluxK<A>
}

@JvmName("filterIsInstance")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A, B> Kind<ForFluxK, A>.filterIsInstance(arg1: Class<B>): FluxK<B> =
    arrow.fx.reactor.FluxK.functorFilter().run {
  this@filterIsInstance.filterIsInstance<A, B>(arg1) as arrow.fx.reactor.FluxK<B>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateReactor)
inline fun Companion.functorFilter(): FluxKFunctorFilter = functorFilter_singleton
