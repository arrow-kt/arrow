package arrow.fx.rx2.extensions.maybek.functor

import arrow.Kind
import arrow.core.Tuple2
import arrow.fx.rx2.DeprecateRxJava
import arrow.fx.rx2.ForMaybeK
import arrow.fx.rx2.MaybeK
import arrow.fx.rx2.MaybeK.Companion
import arrow.fx.rx2.extensions.MaybeKFunctor
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
internal val functor_singleton: MaybeKFunctor = object : arrow.fx.rx2.extensions.MaybeKFunctor {}

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForMaybeK, A>.map(arg1: Function1<A, B>): MaybeK<B> =
    arrow.fx.rx2.MaybeK.functor().run {
  this@map.map<A, B>(arg1) as arrow.fx.rx2.MaybeK<B>
}

@JvmName("imap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForMaybeK, A>.imap(arg1: Function1<A, B>, arg2: Function1<B, A>): MaybeK<B> =
    arrow.fx.rx2.MaybeK.functor().run {
  this@imap.imap<A, B>(arg1, arg2) as arrow.fx.rx2.MaybeK<B>
}

@JvmName("lift")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> lift(arg0: Function1<A, B>): Function1<Kind<ForMaybeK, A>, Kind<ForMaybeK, B>> =
    arrow.fx.rx2.MaybeK
   .functor()
   .lift<A, B>(arg0) as kotlin.Function1<arrow.Kind<arrow.fx.rx2.ForMaybeK, A>,
    arrow.Kind<arrow.fx.rx2.ForMaybeK, B>>

@JvmName("void")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> Kind<ForMaybeK, A>.void(): MaybeK<Unit> = arrow.fx.rx2.MaybeK.functor().run {
  this@void.void<A>() as arrow.fx.rx2.MaybeK<kotlin.Unit>
}

@JvmName("fproduct")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForMaybeK, A>.fproduct(arg1: Function1<A, B>): MaybeK<Tuple2<A, B>> =
    arrow.fx.rx2.MaybeK.functor().run {
  this@fproduct.fproduct<A, B>(arg1) as arrow.fx.rx2.MaybeK<arrow.core.Tuple2<A, B>>
}

@JvmName("mapConst")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForMaybeK, A>.mapConst(arg1: B): MaybeK<B> = arrow.fx.rx2.MaybeK.functor().run {
  this@mapConst.mapConst<A, B>(arg1) as arrow.fx.rx2.MaybeK<B>
}

@JvmName("mapConst")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> A.mapConst(arg1: Kind<ForMaybeK, B>): MaybeK<A> = arrow.fx.rx2.MaybeK.functor().run {
  this@mapConst.mapConst<A, B>(arg1) as arrow.fx.rx2.MaybeK<A>
}

@JvmName("tupleLeft")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForMaybeK, A>.tupleLeft(arg1: B): MaybeK<Tuple2<B, A>> =
    arrow.fx.rx2.MaybeK.functor().run {
  this@tupleLeft.tupleLeft<A, B>(arg1) as arrow.fx.rx2.MaybeK<arrow.core.Tuple2<B, A>>
}

@JvmName("tupleRight")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForMaybeK, A>.tupleRight(arg1: B): MaybeK<Tuple2<A, B>> =
    arrow.fx.rx2.MaybeK.functor().run {
  this@tupleRight.tupleRight<A, B>(arg1) as arrow.fx.rx2.MaybeK<arrow.core.Tuple2<A, B>>
}

@JvmName("widen")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <B, A : B> Kind<ForMaybeK, A>.widen(): MaybeK<B> = arrow.fx.rx2.MaybeK.functor().run {
  this@widen.widen<B, A>() as arrow.fx.rx2.MaybeK<B>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateRxJava)
inline fun Companion.functor(): MaybeKFunctor = functor_singleton
