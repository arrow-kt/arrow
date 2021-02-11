package arrow.fx.rx2.extensions.flowablek.applicative

import arrow.Kind
import arrow.fx.rx2.DeprecateRxJava
import arrow.fx.rx2.FlowableK
import arrow.fx.rx2.FlowableK.Companion
import arrow.fx.rx2.ForFlowableK
import arrow.fx.rx2.extensions.FlowableKApplicative
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
internal val applicative_singleton: FlowableKApplicative = object :
    arrow.fx.rx2.extensions.FlowableKApplicative {}

@JvmName("just1")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> A.just(): FlowableK<A> = arrow.fx.rx2.FlowableK.applicative().run {
  this@just.just<A>() as arrow.fx.rx2.FlowableK<A>
}

@JvmName("unit")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun unit(): FlowableK<Unit> = arrow.fx.rx2.FlowableK
   .applicative()
   .unit() as arrow.fx.rx2.FlowableK<kotlin.Unit>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForFlowableK, A>.map(arg1: Function1<A, B>): FlowableK<B> =
    arrow.fx.rx2.FlowableK.applicative().run {
  this@map.map<A, B>(arg1) as arrow.fx.rx2.FlowableK<B>
}

@JvmName("replicate")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> Kind<ForFlowableK, A>.replicate(arg1: Int): FlowableK<List<A>> =
    arrow.fx.rx2.FlowableK.applicative().run {
  this@replicate.replicate<A>(arg1) as arrow.fx.rx2.FlowableK<kotlin.collections.List<A>>
}

@JvmName("replicate")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> Kind<ForFlowableK, A>.replicate(arg1: Int, arg2: Monoid<A>): FlowableK<A> =
    arrow.fx.rx2.FlowableK.applicative().run {
  this@replicate.replicate<A>(arg1, arg2) as arrow.fx.rx2.FlowableK<A>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateRxJava)
inline fun Companion.applicative(): FlowableKApplicative = applicative_singleton
