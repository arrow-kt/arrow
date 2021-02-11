package arrow.fx.rx2.extensions.flowablek.monad

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.Tuple2
import arrow.fx.rx2.DeprecateRxJava
import arrow.fx.rx2.FlowableK
import arrow.fx.rx2.FlowableK.Companion
import arrow.fx.rx2.ForFlowableK
import arrow.fx.rx2.extensions.FlowableKMonad
import kotlin.Boolean
import kotlin.Deprecated
import kotlin.Function0
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val monad_singleton: FlowableKMonad = object : arrow.fx.rx2.extensions.FlowableKMonad {}

@JvmName("flatMap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForFlowableK, A>.flatMap(arg1: Function1<A, Kind<ForFlowableK, B>>): FlowableK<B> =
    arrow.fx.rx2.FlowableK.monad().run {
  this@flatMap.flatMap<A, B>(arg1) as arrow.fx.rx2.FlowableK<B>
}

@JvmName("tailRecM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> tailRecM(arg0: A, arg1: Function1<A, Kind<ForFlowableK, Either<A, B>>>): FlowableK<B> =
    arrow.fx.rx2.FlowableK
   .monad()
   .tailRecM<A, B>(arg0, arg1) as arrow.fx.rx2.FlowableK<B>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForFlowableK, A>.map(arg1: Function1<A, B>): FlowableK<B> =
    arrow.fx.rx2.FlowableK.monad().run {
  this@map.map<A, B>(arg1) as arrow.fx.rx2.FlowableK<B>
}

@JvmName("ap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForFlowableK, A>.ap(arg1: Kind<ForFlowableK, Function1<A, B>>): FlowableK<B> =
    arrow.fx.rx2.FlowableK.monad().run {
  this@ap.ap<A, B>(arg1) as arrow.fx.rx2.FlowableK<B>
}

@JvmName("flatten")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> Kind<ForFlowableK, Kind<ForFlowableK, A>>.flatten(): FlowableK<A> =
    arrow.fx.rx2.FlowableK.monad().run {
  this@flatten.flatten<A>() as arrow.fx.rx2.FlowableK<A>
}

@JvmName("followedBy")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForFlowableK, A>.followedBy(arg1: Kind<ForFlowableK, B>): FlowableK<B> =
    arrow.fx.rx2.FlowableK.monad().run {
  this@followedBy.followedBy<A, B>(arg1) as arrow.fx.rx2.FlowableK<B>
}

@JvmName("apTap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForFlowableK, A>.apTap(arg1: Kind<ForFlowableK, B>): FlowableK<A> =
    arrow.fx.rx2.FlowableK.monad().run {
  this@apTap.apTap<A, B>(arg1) as arrow.fx.rx2.FlowableK<A>
}

@JvmName("followedByEval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForFlowableK, A>.followedByEval(arg1: Eval<Kind<ForFlowableK, B>>): FlowableK<B> =
    arrow.fx.rx2.FlowableK.monad().run {
  this@followedByEval.followedByEval<A, B>(arg1) as arrow.fx.rx2.FlowableK<B>
}

@JvmName("effectM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForFlowableK, A>.effectM(arg1: Function1<A, Kind<ForFlowableK, B>>): FlowableK<A> =
    arrow.fx.rx2.FlowableK.monad().run {
  this@effectM.effectM<A, B>(arg1) as arrow.fx.rx2.FlowableK<A>
}

@JvmName("flatTap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForFlowableK, A>.flatTap(arg1: Function1<A, Kind<ForFlowableK, B>>): FlowableK<A> =
    arrow.fx.rx2.FlowableK.monad().run {
  this@flatTap.flatTap<A, B>(arg1) as arrow.fx.rx2.FlowableK<A>
}

@JvmName("productL")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForFlowableK, A>.productL(arg1: Kind<ForFlowableK, B>): FlowableK<A> =
    arrow.fx.rx2.FlowableK.monad().run {
  this@productL.productL<A, B>(arg1) as arrow.fx.rx2.FlowableK<A>
}

@JvmName("forEffect")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForFlowableK, A>.forEffect(arg1: Kind<ForFlowableK, B>): FlowableK<A> =
    arrow.fx.rx2.FlowableK.monad().run {
  this@forEffect.forEffect<A, B>(arg1) as arrow.fx.rx2.FlowableK<A>
}

@JvmName("productLEval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForFlowableK, A>.productLEval(arg1: Eval<Kind<ForFlowableK, B>>): FlowableK<A> =
    arrow.fx.rx2.FlowableK.monad().run {
  this@productLEval.productLEval<A, B>(arg1) as arrow.fx.rx2.FlowableK<A>
}

@JvmName("forEffectEval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForFlowableK, A>.forEffectEval(arg1: Eval<Kind<ForFlowableK, B>>): FlowableK<A> =
    arrow.fx.rx2.FlowableK.monad().run {
  this@forEffectEval.forEffectEval<A, B>(arg1) as arrow.fx.rx2.FlowableK<A>
}

@JvmName("mproduct")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForFlowableK, A>.mproduct(arg1: Function1<A, Kind<ForFlowableK, B>>):
    FlowableK<Tuple2<A, B>> = arrow.fx.rx2.FlowableK.monad().run {
  this@mproduct.mproduct<A, B>(arg1) as arrow.fx.rx2.FlowableK<arrow.core.Tuple2<A, B>>
}

@JvmName("ifM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <B> Kind<ForFlowableK, Boolean>.ifM(
  arg1: Function0<Kind<ForFlowableK, B>>,
  arg2: Function0<Kind<ForFlowableK, B>>
): FlowableK<B> = arrow.fx.rx2.FlowableK.monad().run {
  this@ifM.ifM<B>(arg1, arg2) as arrow.fx.rx2.FlowableK<B>
}

@JvmName("selectM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForFlowableK, Either<A, B>>.selectM(arg1: Kind<ForFlowableK, Function1<A, B>>):
    FlowableK<B> = arrow.fx.rx2.FlowableK.monad().run {
  this@selectM.selectM<A, B>(arg1) as arrow.fx.rx2.FlowableK<B>
}

@JvmName("select")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForFlowableK, Either<A, B>>.select(arg1: Kind<ForFlowableK, Function1<A, B>>):
    FlowableK<B> = arrow.fx.rx2.FlowableK.monad().run {
  this@select.select<A, B>(arg1) as arrow.fx.rx2.FlowableK<B>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateRxJava)
inline fun Companion.monad(): FlowableKMonad = monad_singleton
