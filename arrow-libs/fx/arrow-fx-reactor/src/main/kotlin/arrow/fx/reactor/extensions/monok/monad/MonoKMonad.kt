package arrow.fx.reactor.extensions.monok.monad

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.Tuple2
import arrow.fx.reactor.DeprecateReactor
import arrow.fx.reactor.ForMonoK
import arrow.fx.reactor.MonoK
import arrow.fx.reactor.MonoK.Companion
import arrow.fx.reactor.extensions.MonoKMonad
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
internal val monad_singleton: MonoKMonad = object : arrow.fx.reactor.extensions.MonoKMonad {}

@JvmName("flatMap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A, B> Kind<ForMonoK, A>.flatMap(arg1: Function1<A, Kind<ForMonoK, B>>): MonoK<B> =
    arrow.fx.reactor.MonoK.monad().run {
  this@flatMap.flatMap<A, B>(arg1) as arrow.fx.reactor.MonoK<B>
}

@JvmName("tailRecM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A, B> tailRecM(arg0: A, arg1: Function1<A, Kind<ForMonoK, Either<A, B>>>): MonoK<B> =
    arrow.fx.reactor.MonoK
   .monad()
   .tailRecM<A, B>(arg0, arg1) as arrow.fx.reactor.MonoK<B>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A, B> Kind<ForMonoK, A>.map(arg1: Function1<A, B>): MonoK<B> =
    arrow.fx.reactor.MonoK.monad().run {
  this@map.map<A, B>(arg1) as arrow.fx.reactor.MonoK<B>
}

@JvmName("ap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A, B> Kind<ForMonoK, A>.ap(arg1: Kind<ForMonoK, Function1<A, B>>): MonoK<B> =
    arrow.fx.reactor.MonoK.monad().run {
  this@ap.ap<A, B>(arg1) as arrow.fx.reactor.MonoK<B>
}

@JvmName("flatten")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A> Kind<ForMonoK, Kind<ForMonoK, A>>.flatten(): MonoK<A> = arrow.fx.reactor.MonoK.monad().run {
  this@flatten.flatten<A>() as arrow.fx.reactor.MonoK<A>
}

@JvmName("followedBy")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A, B> Kind<ForMonoK, A>.followedBy(arg1: Kind<ForMonoK, B>): MonoK<B> =
    arrow.fx.reactor.MonoK.monad().run {
  this@followedBy.followedBy<A, B>(arg1) as arrow.fx.reactor.MonoK<B>
}

@JvmName("apTap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A, B> Kind<ForMonoK, A>.apTap(arg1: Kind<ForMonoK, B>): MonoK<A> =
    arrow.fx.reactor.MonoK.monad().run {
  this@apTap.apTap<A, B>(arg1) as arrow.fx.reactor.MonoK<A>
}

@JvmName("followedByEval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A, B> Kind<ForMonoK, A>.followedByEval(arg1: Eval<Kind<ForMonoK, B>>): MonoK<B> =
    arrow.fx.reactor.MonoK.monad().run {
  this@followedByEval.followedByEval<A, B>(arg1) as arrow.fx.reactor.MonoK<B>
}

@JvmName("effectM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A, B> Kind<ForMonoK, A>.effectM(arg1: Function1<A, Kind<ForMonoK, B>>): MonoK<A> =
    arrow.fx.reactor.MonoK.monad().run {
  this@effectM.effectM<A, B>(arg1) as arrow.fx.reactor.MonoK<A>
}

@JvmName("flatTap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A, B> Kind<ForMonoK, A>.flatTap(arg1: Function1<A, Kind<ForMonoK, B>>): MonoK<A> =
    arrow.fx.reactor.MonoK.monad().run {
  this@flatTap.flatTap<A, B>(arg1) as arrow.fx.reactor.MonoK<A>
}

@JvmName("productL")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A, B> Kind<ForMonoK, A>.productL(arg1: Kind<ForMonoK, B>): MonoK<A> =
    arrow.fx.reactor.MonoK.monad().run {
  this@productL.productL<A, B>(arg1) as arrow.fx.reactor.MonoK<A>
}

@JvmName("forEffect")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A, B> Kind<ForMonoK, A>.forEffect(arg1: Kind<ForMonoK, B>): MonoK<A> =
    arrow.fx.reactor.MonoK.monad().run {
  this@forEffect.forEffect<A, B>(arg1) as arrow.fx.reactor.MonoK<A>
}

@JvmName("productLEval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A, B> Kind<ForMonoK, A>.productLEval(arg1: Eval<Kind<ForMonoK, B>>): MonoK<A> =
    arrow.fx.reactor.MonoK.monad().run {
  this@productLEval.productLEval<A, B>(arg1) as arrow.fx.reactor.MonoK<A>
}

@JvmName("forEffectEval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A, B> Kind<ForMonoK, A>.forEffectEval(arg1: Eval<Kind<ForMonoK, B>>): MonoK<A> =
    arrow.fx.reactor.MonoK.monad().run {
  this@forEffectEval.forEffectEval<A, B>(arg1) as arrow.fx.reactor.MonoK<A>
}

@JvmName("mproduct")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A, B> Kind<ForMonoK, A>.mproduct(arg1: Function1<A, Kind<ForMonoK, B>>): MonoK<Tuple2<A, B>> =
    arrow.fx.reactor.MonoK.monad().run {
  this@mproduct.mproduct<A, B>(arg1) as arrow.fx.reactor.MonoK<arrow.core.Tuple2<A, B>>
}

@JvmName("ifM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <B> Kind<ForMonoK, Boolean>.ifM(
  arg1: Function0<Kind<ForMonoK, B>>,
  arg2: Function0<Kind<ForMonoK, B>>
): MonoK<B> = arrow.fx.reactor.MonoK.monad().run {
  this@ifM.ifM<B>(arg1, arg2) as arrow.fx.reactor.MonoK<B>
}

@JvmName("selectM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A, B> Kind<ForMonoK, Either<A, B>>.selectM(arg1: Kind<ForMonoK, Function1<A, B>>): MonoK<B> =
    arrow.fx.reactor.MonoK.monad().run {
  this@selectM.selectM<A, B>(arg1) as arrow.fx.reactor.MonoK<B>
}

@JvmName("select")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A, B> Kind<ForMonoK, Either<A, B>>.select(arg1: Kind<ForMonoK, Function1<A, B>>): MonoK<B> =
    arrow.fx.reactor.MonoK.monad().run {
  this@select.select<A, B>(arg1) as arrow.fx.reactor.MonoK<B>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateReactor)
inline fun Companion.monad(): MonoKMonad = monad_singleton
