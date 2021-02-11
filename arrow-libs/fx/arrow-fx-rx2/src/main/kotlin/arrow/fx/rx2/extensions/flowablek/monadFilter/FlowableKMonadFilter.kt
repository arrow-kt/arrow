package arrow.fx.rx2.extensions.flowablek.monadFilter

import arrow.Kind
import arrow.core.Option
import arrow.fx.rx2.DeprecateRxJava
import arrow.fx.rx2.FlowableK
import arrow.fx.rx2.FlowableK.Companion
import arrow.fx.rx2.ForFlowableK
import arrow.fx.rx2.extensions.FlowableKMonadFilter
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
internal val monadFilter_singleton: FlowableKMonadFilter = object :
    arrow.fx.rx2.extensions.FlowableKMonadFilter {}

@JvmName("filterMap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForFlowableK, A>.filterMap(arg1: Function1<A, Option<B>>): FlowableK<B> =
    arrow.fx.rx2.FlowableK.monadFilter().run {
  this@filterMap.filterMap<A, B>(arg1) as arrow.fx.rx2.FlowableK<B>
}

@JvmName("bindingFilter")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <B> bindingFilter(arg0: suspend MonadFilterSyntax<ForFlowableK>.() -> B): FlowableK<B> =
    arrow.fx.rx2.FlowableK
   .monadFilter()
   .bindingFilter<B>(arg0) as arrow.fx.rx2.FlowableK<B>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateRxJava)
inline fun Companion.monadFilter(): FlowableKMonadFilter = monadFilter_singleton
