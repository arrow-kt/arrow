package arrow.fx.rx2.extensions.maybek.monad

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.Tuple2
import arrow.fx.rx2.DeprecateRxJava
import arrow.fx.rx2.ForMaybeK
import arrow.fx.rx2.MaybeK
import arrow.fx.rx2.MaybeK.Companion
import arrow.fx.rx2.extensions.MaybeKMonad
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
internal val monad_singleton: MaybeKMonad = object : arrow.fx.rx2.extensions.MaybeKMonad {}

@JvmName("flatMap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForMaybeK, A>.flatMap(arg1: Function1<A, Kind<ForMaybeK, B>>): MaybeK<B> =
  arrow.fx.rx2.MaybeK.monad().run {
    this@flatMap.flatMap<A, B>(arg1) as arrow.fx.rx2.MaybeK<B>
  }

@JvmName("tailRecM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> tailRecM(arg0: A, arg1: Function1<A, Kind<ForMaybeK, Either<A, B>>>): MaybeK<B> =
  arrow.fx.rx2.MaybeK
    .monad()
    .tailRecM<A, B>(arg0, arg1) as arrow.fx.rx2.MaybeK<B>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForMaybeK, A>.map(arg1: Function1<A, B>): MaybeK<B> =
  arrow.fx.rx2.MaybeK.monad().run {
    this@map.map<A, B>(arg1) as arrow.fx.rx2.MaybeK<B>
  }

@JvmName("ap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForMaybeK, A>.ap(arg1: Kind<ForMaybeK, Function1<A, B>>): MaybeK<B> =
  arrow.fx.rx2.MaybeK.monad().run {
    this@ap.ap<A, B>(arg1) as arrow.fx.rx2.MaybeK<B>
  }

@JvmName("flatten")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> Kind<ForMaybeK, Kind<ForMaybeK, A>>.flatten(): MaybeK<A> = arrow.fx.rx2.MaybeK.monad().run {
  this@flatten.flatten<A>() as arrow.fx.rx2.MaybeK<A>
}

@JvmName("followedBy")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForMaybeK, A>.followedBy(arg1: Kind<ForMaybeK, B>): MaybeK<B> =
  arrow.fx.rx2.MaybeK.monad().run {
    this@followedBy.followedBy<A, B>(arg1) as arrow.fx.rx2.MaybeK<B>
  }

@JvmName("apTap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForMaybeK, A>.apTap(arg1: Kind<ForMaybeK, B>): MaybeK<A> =
  arrow.fx.rx2.MaybeK.monad().run {
    this@apTap.apTap<A, B>(arg1) as arrow.fx.rx2.MaybeK<A>
  }

@JvmName("followedByEval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForMaybeK, A>.followedByEval(arg1: Eval<Kind<ForMaybeK, B>>): MaybeK<B> =
  arrow.fx.rx2.MaybeK.monad().run {
    this@followedByEval.followedByEval<A, B>(arg1) as arrow.fx.rx2.MaybeK<B>
  }

@JvmName("effectM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForMaybeK, A>.effectM(arg1: Function1<A, Kind<ForMaybeK, B>>): MaybeK<A> =
  arrow.fx.rx2.MaybeK.monad().run {
    this@effectM.effectM<A, B>(arg1) as arrow.fx.rx2.MaybeK<A>
  }

@JvmName("flatTap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForMaybeK, A>.flatTap(arg1: Function1<A, Kind<ForMaybeK, B>>): MaybeK<A> =
  arrow.fx.rx2.MaybeK.monad().run {
    this@flatTap.flatTap<A, B>(arg1) as arrow.fx.rx2.MaybeK<A>
  }

@JvmName("productL")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForMaybeK, A>.productL(arg1: Kind<ForMaybeK, B>): MaybeK<A> =
  arrow.fx.rx2.MaybeK.monad().run {
    this@productL.productL<A, B>(arg1) as arrow.fx.rx2.MaybeK<A>
  }

@JvmName("forEffect")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForMaybeK, A>.forEffect(arg1: Kind<ForMaybeK, B>): MaybeK<A> =
  arrow.fx.rx2.MaybeK.monad().run {
    this@forEffect.forEffect<A, B>(arg1) as arrow.fx.rx2.MaybeK<A>
  }

@JvmName("productLEval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForMaybeK, A>.productLEval(arg1: Eval<Kind<ForMaybeK, B>>): MaybeK<A> =
  arrow.fx.rx2.MaybeK.monad().run {
    this@productLEval.productLEval<A, B>(arg1) as arrow.fx.rx2.MaybeK<A>
  }

@JvmName("forEffectEval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForMaybeK, A>.forEffectEval(arg1: Eval<Kind<ForMaybeK, B>>): MaybeK<A> =
  arrow.fx.rx2.MaybeK.monad().run {
    this@forEffectEval.forEffectEval<A, B>(arg1) as arrow.fx.rx2.MaybeK<A>
  }

@JvmName("mproduct")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForMaybeK, A>.mproduct(arg1: Function1<A, Kind<ForMaybeK, B>>): MaybeK<Tuple2<A, B>> = arrow.fx.rx2.MaybeK.monad().run {
  this@mproduct.mproduct<A, B>(arg1) as arrow.fx.rx2.MaybeK<arrow.core.Tuple2<A, B>>
}

@JvmName("ifM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)

@Deprecated(DeprecateRxJava)
fun <B> Kind<ForMaybeK, Boolean>.ifM(
  arg1: Function0<Kind<ForMaybeK, B>>,
  arg2: Function0<Kind<ForMaybeK, B>>
): MaybeK<B> = arrow.fx.rx2.MaybeK.monad().run {
  this@ifM.ifM<B>(arg1, arg2) as arrow.fx.rx2.MaybeK<B>
}

@JvmName("selectM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForMaybeK, Either<A, B>>.selectM(arg1: Kind<ForMaybeK, Function1<A, B>>): MaybeK<B> = arrow.fx.rx2.MaybeK.monad().run {
  this@selectM.selectM<A, B>(arg1) as arrow.fx.rx2.MaybeK<B>
}

@JvmName("select")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForMaybeK, Either<A, B>>.select(arg1: Kind<ForMaybeK, Function1<A, B>>): MaybeK<B> =
  arrow.fx.rx2.MaybeK.monad().run {
    this@select.select<A, B>(arg1) as arrow.fx.rx2.MaybeK<B>
  }

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateRxJava)
inline fun Companion.monad(): MaybeKMonad = monad_singleton
