package arrow.fx.rx2.extensions.flowablek.traverse

import arrow.Kind
import arrow.fx.rx2.DeprecateRxJava
import arrow.fx.rx2.FlowableK
import arrow.fx.rx2.FlowableK.Companion
import arrow.fx.rx2.ForFlowableK
import arrow.fx.rx2.extensions.FlowableKTraverse
import arrow.typeclasses.Applicative
import arrow.typeclasses.Monad
import kotlin.Deprecated
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val traverse_singleton: FlowableKTraverse = object :
    arrow.fx.rx2.extensions.FlowableKTraverse {}

@JvmName("traverse")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <G, A, B> Kind<ForFlowableK, A>.traverse(arg1: Applicative<G>, arg2: Function1<A, Kind<G, B>>):
    Kind<G, Kind<ForFlowableK, B>> = arrow.fx.rx2.FlowableK.traverse().run {
  this@traverse.traverse<G, A, B>(arg1, arg2) as arrow.Kind<G, arrow.Kind<arrow.fx.rx2.ForFlowableK,
    B>>
}

@JvmName("sequence")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <G, A> Kind<ForFlowableK, Kind<G, A>>.sequence(arg1: Applicative<G>): Kind<G, Kind<ForFlowableK,
    A>> = arrow.fx.rx2.FlowableK.traverse().run {
  this@sequence.sequence<G, A>(arg1) as arrow.Kind<G, arrow.Kind<arrow.fx.rx2.ForFlowableK, A>>
}

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForFlowableK, A>.map(arg1: Function1<A, B>): FlowableK<B> =
    arrow.fx.rx2.FlowableK.traverse().run {
  this@map.map<A, B>(arg1) as arrow.fx.rx2.FlowableK<B>
}

@JvmName("flatTraverse")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <G, A, B> Kind<ForFlowableK, A>.flatTraverse(
  arg1: Monad<ForFlowableK>,
  arg2: Applicative<G>,
  arg3: Function1<A, Kind<G, Kind<ForFlowableK, B>>>
): Kind<G, Kind<ForFlowableK, B>> = arrow.fx.rx2.FlowableK.traverse().run {
  this@flatTraverse.flatTraverse<G, A, B>(arg1, arg2, arg3) as arrow.Kind<G,
    arrow.Kind<arrow.fx.rx2.ForFlowableK, B>>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateRxJava)
inline fun Companion.traverse(): FlowableKTraverse = traverse_singleton
