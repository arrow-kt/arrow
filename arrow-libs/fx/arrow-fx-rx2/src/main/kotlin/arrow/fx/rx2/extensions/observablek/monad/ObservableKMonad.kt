package arrow.fx.rx2.extensions.observablek.monad

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.Tuple2
import arrow.fx.rx2.DeprecateRxJava
import arrow.fx.rx2.ForObservableK
import arrow.fx.rx2.ObservableK
import arrow.fx.rx2.ObservableK.Companion
import arrow.fx.rx2.extensions.ObservableKMonad
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
internal val monad_singleton: ObservableKMonad = object : arrow.fx.rx2.extensions.ObservableKMonad
{}

@JvmName("flatMap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForObservableK, A>.flatMap(arg1: Function1<A, Kind<ForObservableK, B>>):
  ObservableK<B> = arrow.fx.rx2.ObservableK.monad().run {
    this@flatMap.flatMap<A, B>(arg1) as arrow.fx.rx2.ObservableK<B>
  }

@JvmName("tailRecM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> tailRecM(arg0: A, arg1: Function1<A, Kind<ForObservableK, Either<A, B>>>): ObservableK<B> =
  arrow.fx.rx2.ObservableK
    .monad()
    .tailRecM<A, B>(arg0, arg1) as arrow.fx.rx2.ObservableK<B>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForObservableK, A>.map(arg1: Function1<A, B>): ObservableK<B> =
  arrow.fx.rx2.ObservableK.monad().run {
    this@map.map<A, B>(arg1) as arrow.fx.rx2.ObservableK<B>
  }

@JvmName("ap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForObservableK, A>.ap(arg1: Kind<ForObservableK, Function1<A, B>>): ObservableK<B> =
  arrow.fx.rx2.ObservableK.monad().run {
    this@ap.ap<A, B>(arg1) as arrow.fx.rx2.ObservableK<B>
  }

@JvmName("flatten")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> Kind<ForObservableK, Kind<ForObservableK, A>>.flatten(): ObservableK<A> =
  arrow.fx.rx2.ObservableK.monad().run {
    this@flatten.flatten<A>() as arrow.fx.rx2.ObservableK<A>
  }

@JvmName("followedBy")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForObservableK, A>.followedBy(arg1: Kind<ForObservableK, B>): ObservableK<B> =
  arrow.fx.rx2.ObservableK.monad().run {
    this@followedBy.followedBy<A, B>(arg1) as arrow.fx.rx2.ObservableK<B>
  }

@JvmName("apTap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForObservableK, A>.apTap(arg1: Kind<ForObservableK, B>): ObservableK<A> =
  arrow.fx.rx2.ObservableK.monad().run {
    this@apTap.apTap<A, B>(arg1) as arrow.fx.rx2.ObservableK<A>
  }

@JvmName("followedByEval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForObservableK, A>.followedByEval(arg1: Eval<Kind<ForObservableK, B>>):
  ObservableK<B> = arrow.fx.rx2.ObservableK.monad().run {
    this@followedByEval.followedByEval<A, B>(arg1) as arrow.fx.rx2.ObservableK<B>
  }

@JvmName("effectM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForObservableK, A>.effectM(arg1: Function1<A, Kind<ForObservableK, B>>):
  ObservableK<A> = arrow.fx.rx2.ObservableK.monad().run {
    this@effectM.effectM<A, B>(arg1) as arrow.fx.rx2.ObservableK<A>
  }

@JvmName("flatTap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForObservableK, A>.flatTap(arg1: Function1<A, Kind<ForObservableK, B>>):
  ObservableK<A> = arrow.fx.rx2.ObservableK.monad().run {
    this@flatTap.flatTap<A, B>(arg1) as arrow.fx.rx2.ObservableK<A>
  }

@JvmName("productL")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForObservableK, A>.productL(arg1: Kind<ForObservableK, B>): ObservableK<A> =
  arrow.fx.rx2.ObservableK.monad().run {
    this@productL.productL<A, B>(arg1) as arrow.fx.rx2.ObservableK<A>
  }

@JvmName("forEffect")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForObservableK, A>.forEffect(arg1: Kind<ForObservableK, B>): ObservableK<A> =
  arrow.fx.rx2.ObservableK.monad().run {
    this@forEffect.forEffect<A, B>(arg1) as arrow.fx.rx2.ObservableK<A>
  }

@JvmName("productLEval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForObservableK, A>.productLEval(arg1: Eval<Kind<ForObservableK, B>>): ObservableK<A> =
  arrow.fx.rx2.ObservableK.monad().run {
    this@productLEval.productLEval<A, B>(arg1) as arrow.fx.rx2.ObservableK<A>
  }

@JvmName("forEffectEval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForObservableK, A>.forEffectEval(arg1: Eval<Kind<ForObservableK, B>>):
  ObservableK<A> = arrow.fx.rx2.ObservableK.monad().run {
    this@forEffectEval.forEffectEval<A, B>(arg1) as arrow.fx.rx2.ObservableK<A>
  }

@JvmName("mproduct")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForObservableK, A>.mproduct(arg1: Function1<A, Kind<ForObservableK, B>>):
  ObservableK<Tuple2<A, B>> = arrow.fx.rx2.ObservableK.monad().run {
    this@mproduct.mproduct<A, B>(arg1) as arrow.fx.rx2.ObservableK<arrow.core.Tuple2<A, B>>
  }

@JvmName("ifM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <B> Kind<ForObservableK, Boolean>.ifM(
  arg1: Function0<Kind<ForObservableK, B>>,
  arg2: Function0<Kind<ForObservableK, B>>
): ObservableK<B> =
  arrow.fx.rx2.ObservableK.monad().run {
    this@ifM.ifM<B>(arg1, arg2) as arrow.fx.rx2.ObservableK<B>
  }

@JvmName("selectM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForObservableK, Either<A, B>>.selectM(arg1: Kind<ForObservableK, Function1<A, B>>):
  ObservableK<B> = arrow.fx.rx2.ObservableK.monad().run {
    this@selectM.selectM<A, B>(arg1) as arrow.fx.rx2.ObservableK<B>
  }

@JvmName("select")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForObservableK, Either<A, B>>.select(arg1: Kind<ForObservableK, Function1<A, B>>):
  ObservableK<B> = arrow.fx.rx2.ObservableK.monad().run {
    this@select.select<A, B>(arg1) as arrow.fx.rx2.ObservableK<B>
  }

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateRxJava)
inline fun Companion.monad(): ObservableKMonad = monad_singleton
