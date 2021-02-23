package arrow.fx.rx2.extensions.singlek.monad

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.Tuple2
import arrow.fx.rx2.DeprecateRxJava
import arrow.fx.rx2.ForSingleK
import arrow.fx.rx2.SingleK
import arrow.fx.rx2.SingleK.Companion
import arrow.fx.rx2.extensions.SingleKMonad
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
internal val monad_singleton: SingleKMonad = object : arrow.fx.rx2.extensions.SingleKMonad {}

@JvmName("flatMap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForSingleK, A>.flatMap(arg1: Function1<A, Kind<ForSingleK, B>>): SingleK<B> =
  arrow.fx.rx2.SingleK.monad().run {
    this@flatMap.flatMap<A, B>(arg1) as arrow.fx.rx2.SingleK<B>
  }

@JvmName("tailRecM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> tailRecM(arg0: A, arg1: Function1<A, Kind<ForSingleK, Either<A, B>>>): SingleK<B> =
  arrow.fx.rx2.SingleK
    .monad()
    .tailRecM<A, B>(arg0, arg1) as arrow.fx.rx2.SingleK<B>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForSingleK, A>.map(arg1: Function1<A, B>): SingleK<B> =
  arrow.fx.rx2.SingleK.monad().run {
    this@map.map<A, B>(arg1) as arrow.fx.rx2.SingleK<B>
  }

@JvmName("ap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForSingleK, A>.ap(arg1: Kind<ForSingleK, Function1<A, B>>): SingleK<B> =
  arrow.fx.rx2.SingleK.monad().run {
    this@ap.ap<A, B>(arg1) as arrow.fx.rx2.SingleK<B>
  }

@JvmName("flatten")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> Kind<ForSingleK, Kind<ForSingleK, A>>.flatten(): SingleK<A> =
  arrow.fx.rx2.SingleK.monad().run {
    this@flatten.flatten<A>() as arrow.fx.rx2.SingleK<A>
  }

@JvmName("followedBy")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForSingleK, A>.followedBy(arg1: Kind<ForSingleK, B>): SingleK<B> =
  arrow.fx.rx2.SingleK.monad().run {
    this@followedBy.followedBy<A, B>(arg1) as arrow.fx.rx2.SingleK<B>
  }

@JvmName("apTap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForSingleK, A>.apTap(arg1: Kind<ForSingleK, B>): SingleK<A> =
  arrow.fx.rx2.SingleK.monad().run {
    this@apTap.apTap<A, B>(arg1) as arrow.fx.rx2.SingleK<A>
  }

@JvmName("followedByEval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForSingleK, A>.followedByEval(arg1: Eval<Kind<ForSingleK, B>>): SingleK<B> =
  arrow.fx.rx2.SingleK.monad().run {
    this@followedByEval.followedByEval<A, B>(arg1) as arrow.fx.rx2.SingleK<B>
  }

@JvmName("effectM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForSingleK, A>.effectM(arg1: Function1<A, Kind<ForSingleK, B>>): SingleK<A> =
  arrow.fx.rx2.SingleK.monad().run {
    this@effectM.effectM<A, B>(arg1) as arrow.fx.rx2.SingleK<A>
  }

@JvmName("flatTap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForSingleK, A>.flatTap(arg1: Function1<A, Kind<ForSingleK, B>>): SingleK<A> =
  arrow.fx.rx2.SingleK.monad().run {
    this@flatTap.flatTap<A, B>(arg1) as arrow.fx.rx2.SingleK<A>
  }

@JvmName("productL")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForSingleK, A>.productL(arg1: Kind<ForSingleK, B>): SingleK<A> =
  arrow.fx.rx2.SingleK.monad().run {
    this@productL.productL<A, B>(arg1) as arrow.fx.rx2.SingleK<A>
  }

@JvmName("forEffect")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForSingleK, A>.forEffect(arg1: Kind<ForSingleK, B>): SingleK<A> =
  arrow.fx.rx2.SingleK.monad().run {
    this@forEffect.forEffect<A, B>(arg1) as arrow.fx.rx2.SingleK<A>
  }

@JvmName("productLEval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForSingleK, A>.productLEval(arg1: Eval<Kind<ForSingleK, B>>): SingleK<A> =
  arrow.fx.rx2.SingleK.monad().run {
    this@productLEval.productLEval<A, B>(arg1) as arrow.fx.rx2.SingleK<A>
  }

@JvmName("forEffectEval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForSingleK, A>.forEffectEval(arg1: Eval<Kind<ForSingleK, B>>): SingleK<A> =
  arrow.fx.rx2.SingleK.monad().run {
    this@forEffectEval.forEffectEval<A, B>(arg1) as arrow.fx.rx2.SingleK<A>
  }

@JvmName("mproduct")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForSingleK, A>.mproduct(arg1: Function1<A, Kind<ForSingleK, B>>): SingleK<Tuple2<A,
    B>> = arrow.fx.rx2.SingleK.monad().run {
  this@mproduct.mproduct<A, B>(arg1) as arrow.fx.rx2.SingleK<arrow.core.Tuple2<A, B>>
}

@JvmName("ifM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <B> Kind<ForSingleK, Boolean>.ifM(
  arg1: Function0<Kind<ForSingleK, B>>,
  arg2: Function0<Kind<ForSingleK, B>>
): SingleK<B> = arrow.fx.rx2.SingleK.monad().run {
  this@ifM.ifM<B>(arg1, arg2) as arrow.fx.rx2.SingleK<B>
}

@JvmName("selectM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForSingleK, Either<A, B>>.selectM(arg1: Kind<ForSingleK, Function1<A, B>>):
  SingleK<B> = arrow.fx.rx2.SingleK.monad().run {
    this@selectM.selectM<A, B>(arg1) as arrow.fx.rx2.SingleK<B>
  }

@JvmName("select")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForSingleK, Either<A, B>>.select(arg1: Kind<ForSingleK, Function1<A, B>>):
  SingleK<B> = arrow.fx.rx2.SingleK.monad().run {
    this@select.select<A, B>(arg1) as arrow.fx.rx2.SingleK<B>
  }

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateRxJava)
inline fun Companion.monad(): SingleKMonad = monad_singleton
