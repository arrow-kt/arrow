package arrow.fx.reactor.extensions.fluxk.functor

import arrow.Kind
import arrow.core.Tuple2
import arrow.fx.reactor.DeprecateReactor
import arrow.fx.reactor.FluxK
import arrow.fx.reactor.FluxK.Companion
import arrow.fx.reactor.ForFluxK
import arrow.fx.reactor.extensions.FluxKFunctor
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
internal val functor_singleton: FluxKFunctor = object : arrow.fx.reactor.extensions.FluxKFunctor {}

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A, B> Kind<ForFluxK, A>.map(arg1: Function1<A, B>): FluxK<B> =
    arrow.fx.reactor.FluxK.functor().run {
  this@map.map<A, B>(arg1) as arrow.fx.reactor.FluxK<B>
}

@JvmName("imap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A, B> Kind<ForFluxK, A>.imap(arg1: Function1<A, B>, arg2: Function1<B, A>): FluxK<B> =
    arrow.fx.reactor.FluxK.functor().run {
  this@imap.imap<A, B>(arg1, arg2) as arrow.fx.reactor.FluxK<B>
}

@JvmName("lift")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A, B> lift(arg0: Function1<A, B>): Function1<Kind<ForFluxK, A>, Kind<ForFluxK, B>> =
    arrow.fx.reactor.FluxK
   .functor()
   .lift<A, B>(arg0) as kotlin.Function1<arrow.Kind<arrow.fx.reactor.ForFluxK, A>,
    arrow.Kind<arrow.fx.reactor.ForFluxK, B>>

@JvmName("void")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A> Kind<ForFluxK, A>.void(): FluxK<Unit> = arrow.fx.reactor.FluxK.functor().run {
  this@void.void<A>() as arrow.fx.reactor.FluxK<kotlin.Unit>
}

@JvmName("fproduct")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A, B> Kind<ForFluxK, A>.fproduct(arg1: Function1<A, B>): FluxK<Tuple2<A, B>> =
    arrow.fx.reactor.FluxK.functor().run {
  this@fproduct.fproduct<A, B>(arg1) as arrow.fx.reactor.FluxK<arrow.core.Tuple2<A, B>>
}

@JvmName("mapConst")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A, B> Kind<ForFluxK, A>.mapConst(arg1: B): FluxK<B> = arrow.fx.reactor.FluxK.functor().run {
  this@mapConst.mapConst<A, B>(arg1) as arrow.fx.reactor.FluxK<B>
}

@JvmName("mapConst")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A, B> A.mapConst(arg1: Kind<ForFluxK, B>): FluxK<A> = arrow.fx.reactor.FluxK.functor().run {
  this@mapConst.mapConst<A, B>(arg1) as arrow.fx.reactor.FluxK<A>
}

@JvmName("tupleLeft")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A, B> Kind<ForFluxK, A>.tupleLeft(arg1: B): FluxK<Tuple2<B, A>> =
    arrow.fx.reactor.FluxK.functor().run {
  this@tupleLeft.tupleLeft<A, B>(arg1) as arrow.fx.reactor.FluxK<arrow.core.Tuple2<B, A>>
}

@JvmName("tupleRight")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A, B> Kind<ForFluxK, A>.tupleRight(arg1: B): FluxK<Tuple2<A, B>> =
    arrow.fx.reactor.FluxK.functor().run {
  this@tupleRight.tupleRight<A, B>(arg1) as arrow.fx.reactor.FluxK<arrow.core.Tuple2<A, B>>
}

@JvmName("widen")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <B, A : B> Kind<ForFluxK, A>.widen(): FluxK<B> = arrow.fx.reactor.FluxK.functor().run {
  this@widen.widen<B, A>() as arrow.fx.reactor.FluxK<B>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateReactor)
inline fun Companion.functor(): FluxKFunctor = functor_singleton
