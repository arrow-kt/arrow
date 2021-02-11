package arrow.fx.rx2.extensions.flowablek.functor

import arrow.Kind
import arrow.core.Tuple2
import arrow.fx.rx2.DeprecateRxJava
import arrow.fx.rx2.FlowableK
import arrow.fx.rx2.FlowableK.Companion
import arrow.fx.rx2.ForFlowableK
import arrow.fx.rx2.extensions.FlowableKFunctor
import kotlin.Deprecated
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.Unit
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val functor_singleton: FlowableKFunctor = object : arrow.fx.rx2.extensions.FlowableKFunctor
    {}

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForFlowableK, A>.map(arg1: Function1<A, B>): FlowableK<B> =
    arrow.fx.rx2.FlowableK.functor().run {
  this@map.map<A, B>(arg1) as arrow.fx.rx2.FlowableK<B>
}

@JvmName("imap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForFlowableK, A>.imap(arg1: Function1<A, B>, arg2: Function1<B, A>): FlowableK<B> =
    arrow.fx.rx2.FlowableK.functor().run {
  this@imap.imap<A, B>(arg1, arg2) as arrow.fx.rx2.FlowableK<B>
}

@JvmName("lift")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> lift(arg0: Function1<A, B>): Function1<Kind<ForFlowableK, A>, Kind<ForFlowableK, B>> =
    arrow.fx.rx2.FlowableK
   .functor()
   .lift<A, B>(arg0) as kotlin.Function1<arrow.Kind<arrow.fx.rx2.ForFlowableK, A>,
    arrow.Kind<arrow.fx.rx2.ForFlowableK, B>>

@JvmName("void")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> Kind<ForFlowableK, A>.void(): FlowableK<Unit> = arrow.fx.rx2.FlowableK.functor().run {
  this@void.void<A>() as arrow.fx.rx2.FlowableK<kotlin.Unit>
}

@JvmName("fproduct")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForFlowableK, A>.fproduct(arg1: Function1<A, B>): FlowableK<Tuple2<A, B>> =
    arrow.fx.rx2.FlowableK.functor().run {
  this@fproduct.fproduct<A, B>(arg1) as arrow.fx.rx2.FlowableK<arrow.core.Tuple2<A, B>>
}

@JvmName("mapConst")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForFlowableK, A>.mapConst(arg1: B): FlowableK<B> =
    arrow.fx.rx2.FlowableK.functor().run {
  this@mapConst.mapConst<A, B>(arg1) as arrow.fx.rx2.FlowableK<B>
}

@JvmName("mapConst")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> A.mapConst(arg1: Kind<ForFlowableK, B>): FlowableK<A> =
    arrow.fx.rx2.FlowableK.functor().run {
  this@mapConst.mapConst<A, B>(arg1) as arrow.fx.rx2.FlowableK<A>
}

@JvmName("tupleLeft")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForFlowableK, A>.tupleLeft(arg1: B): FlowableK<Tuple2<B, A>> =
    arrow.fx.rx2.FlowableK.functor().run {
  this@tupleLeft.tupleLeft<A, B>(arg1) as arrow.fx.rx2.FlowableK<arrow.core.Tuple2<B, A>>
}

@JvmName("tupleRight")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForFlowableK, A>.tupleRight(arg1: B): FlowableK<Tuple2<A, B>> =
    arrow.fx.rx2.FlowableK.functor().run {
  this@tupleRight.tupleRight<A, B>(arg1) as arrow.fx.rx2.FlowableK<arrow.core.Tuple2<A, B>>
}

@JvmName("widen")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <B, A : B> Kind<ForFlowableK, A>.widen(): FlowableK<B> = arrow.fx.rx2.FlowableK.functor().run {
  this@widen.widen<B, A>() as arrow.fx.rx2.FlowableK<B>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateRxJava)
inline fun Companion.functor(): FlowableKFunctor = functor_singleton
