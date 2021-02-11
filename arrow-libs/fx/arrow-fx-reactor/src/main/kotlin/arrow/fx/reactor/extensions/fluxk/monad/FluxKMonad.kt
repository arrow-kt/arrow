package arrow.fx.reactor.extensions.fluxk.monad

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.Tuple2
import arrow.fx.reactor.DeprecateReactor
import arrow.fx.reactor.FluxK
import arrow.fx.reactor.FluxK.Companion
import arrow.fx.reactor.ForFluxK
import arrow.fx.reactor.extensions.FluxKMonad
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
internal val monad_singleton: FluxKMonad = object : arrow.fx.reactor.extensions.FluxKMonad {}

@JvmName("flatMap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A, B> Kind<ForFluxK, A>.flatMap(arg1: Function1<A, Kind<ForFluxK, B>>): FluxK<B> =
    arrow.fx.reactor.FluxK.monad().run {
  this@flatMap.flatMap<A, B>(arg1) as arrow.fx.reactor.FluxK<B>
}

@JvmName("tailRecM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A, B> tailRecM(arg0: A, arg1: Function1<A, Kind<ForFluxK, Either<A, B>>>): FluxK<B> =
    arrow.fx.reactor.FluxK
   .monad()
   .tailRecM<A, B>(arg0, arg1) as arrow.fx.reactor.FluxK<B>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A, B> Kind<ForFluxK, A>.map(arg1: Function1<A, B>): FluxK<B> =
    arrow.fx.reactor.FluxK.monad().run {
  this@map.map<A, B>(arg1) as arrow.fx.reactor.FluxK<B>
}

@JvmName("ap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A, B> Kind<ForFluxK, A>.ap(arg1: Kind<ForFluxK, Function1<A, B>>): FluxK<B> =
    arrow.fx.reactor.FluxK.monad().run {
  this@ap.ap<A, B>(arg1) as arrow.fx.reactor.FluxK<B>
}

@JvmName("flatten")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A> Kind<ForFluxK, Kind<ForFluxK, A>>.flatten(): FluxK<A> = arrow.fx.reactor.FluxK.monad().run {
  this@flatten.flatten<A>() as arrow.fx.reactor.FluxK<A>
}

@JvmName("followedBy")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A, B> Kind<ForFluxK, A>.followedBy(arg1: Kind<ForFluxK, B>): FluxK<B> =
    arrow.fx.reactor.FluxK.monad().run {
  this@followedBy.followedBy<A, B>(arg1) as arrow.fx.reactor.FluxK<B>
}

@JvmName("apTap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A, B> Kind<ForFluxK, A>.apTap(arg1: Kind<ForFluxK, B>): FluxK<A> =
    arrow.fx.reactor.FluxK.monad().run {
  this@apTap.apTap<A, B>(arg1) as arrow.fx.reactor.FluxK<A>
}

@JvmName("followedByEval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A, B> Kind<ForFluxK, A>.followedByEval(arg1: Eval<Kind<ForFluxK, B>>): FluxK<B> =
    arrow.fx.reactor.FluxK.monad().run {
  this@followedByEval.followedByEval<A, B>(arg1) as arrow.fx.reactor.FluxK<B>
}

@JvmName("effectM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A, B> Kind<ForFluxK, A>.effectM(arg1: Function1<A, Kind<ForFluxK, B>>): FluxK<A> =
    arrow.fx.reactor.FluxK.monad().run {
  this@effectM.effectM<A, B>(arg1) as arrow.fx.reactor.FluxK<A>
}

@JvmName("flatTap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A, B> Kind<ForFluxK, A>.flatTap(arg1: Function1<A, Kind<ForFluxK, B>>): FluxK<A> =
    arrow.fx.reactor.FluxK.monad().run {
  this@flatTap.flatTap<A, B>(arg1) as arrow.fx.reactor.FluxK<A>
}

@JvmName("productL")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A, B> Kind<ForFluxK, A>.productL(arg1: Kind<ForFluxK, B>): FluxK<A> =
    arrow.fx.reactor.FluxK.monad().run {
  this@productL.productL<A, B>(arg1) as arrow.fx.reactor.FluxK<A>
}

@JvmName("forEffect")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A, B> Kind<ForFluxK, A>.forEffect(arg1: Kind<ForFluxK, B>): FluxK<A> =
    arrow.fx.reactor.FluxK.monad().run {
  this@forEffect.forEffect<A, B>(arg1) as arrow.fx.reactor.FluxK<A>
}

@JvmName("productLEval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A, B> Kind<ForFluxK, A>.productLEval(arg1: Eval<Kind<ForFluxK, B>>): FluxK<A> =
    arrow.fx.reactor.FluxK.monad().run {
  this@productLEval.productLEval<A, B>(arg1) as arrow.fx.reactor.FluxK<A>
}

@JvmName("forEffectEval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A, B> Kind<ForFluxK, A>.forEffectEval(arg1: Eval<Kind<ForFluxK, B>>): FluxK<A> =
    arrow.fx.reactor.FluxK.monad().run {
  this@forEffectEval.forEffectEval<A, B>(arg1) as arrow.fx.reactor.FluxK<A>
}

@JvmName("mproduct")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A, B> Kind<ForFluxK, A>.mproduct(arg1: Function1<A, Kind<ForFluxK, B>>): FluxK<Tuple2<A, B>> =
    arrow.fx.reactor.FluxK.monad().run {
  this@mproduct.mproduct<A, B>(arg1) as arrow.fx.reactor.FluxK<arrow.core.Tuple2<A, B>>
}

@JvmName("ifM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <B> Kind<ForFluxK, Boolean>.ifM(
  arg1: Function0<Kind<ForFluxK, B>>,
  arg2: Function0<Kind<ForFluxK, B>>
): FluxK<B> = arrow.fx.reactor.FluxK.monad().run {
  this@ifM.ifM<B>(arg1, arg2) as arrow.fx.reactor.FluxK<B>
}

@JvmName("selectM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A, B> Kind<ForFluxK, Either<A, B>>.selectM(arg1: Kind<ForFluxK, Function1<A, B>>): FluxK<B> =
    arrow.fx.reactor.FluxK.monad().run {
  this@selectM.selectM<A, B>(arg1) as arrow.fx.reactor.FluxK<B>
}

@JvmName("select")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A, B> Kind<ForFluxK, Either<A, B>>.select(arg1: Kind<ForFluxK, Function1<A, B>>): FluxK<B> =
    arrow.fx.reactor.FluxK.monad().run {
  this@select.select<A, B>(arg1) as arrow.fx.reactor.FluxK<B>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateReactor)
inline fun Companion.monad(): FluxKMonad = monad_singleton
