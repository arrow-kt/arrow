package arrow.fx.rx2.extensions.flowablek.functorFilter

import arrow.Kind
import arrow.core.Option
import arrow.fx.rx2.DeprecateRxJava
import arrow.fx.rx2.FlowableK
import arrow.fx.rx2.FlowableK.Companion
import arrow.fx.rx2.ForFlowableK
import arrow.fx.rx2.extensions.FlowableKFunctorFilter
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
internal val functorFilter_singleton: FlowableKFunctorFilter = object :
    arrow.fx.rx2.extensions.FlowableKFunctorFilter {}

@JvmName("filterMap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForFlowableK, A>.filterMap(arg1: Function1<A, Option<B>>): FlowableK<B> =
    arrow.fx.rx2.FlowableK.functorFilter().run {
  this@filterMap.filterMap<A, B>(arg1) as arrow.fx.rx2.FlowableK<B>
}

@JvmName("flattenOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> Kind<ForFlowableK, Option<A>>.flattenOption(): FlowableK<A> =
    arrow.fx.rx2.FlowableK.functorFilter().run {
  this@flattenOption.flattenOption<A>() as arrow.fx.rx2.FlowableK<A>
}

@JvmName("filter")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> Kind<ForFlowableK, A>.filter(arg1: Function1<A, Boolean>): FlowableK<A> =
    arrow.fx.rx2.FlowableK.functorFilter().run {
  this@filter.filter<A>(arg1) as arrow.fx.rx2.FlowableK<A>
}

@JvmName("filterIsInstance")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForFlowableK, A>.filterIsInstance(arg1: Class<B>): FlowableK<B> =
    arrow.fx.rx2.FlowableK.functorFilter().run {
  this@filterIsInstance.filterIsInstance<A, B>(arg1) as arrow.fx.rx2.FlowableK<B>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateRxJava)
inline fun Companion.functorFilter(): FlowableKFunctorFilter = functorFilter_singleton
