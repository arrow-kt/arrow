package arrow.fx.rx2.extensions.singlek.functor

import arrow.Kind
import arrow.core.Tuple2
import arrow.fx.rx2.DeprecateRxJava
import arrow.fx.rx2.ForSingleK
import arrow.fx.rx2.SingleK
import arrow.fx.rx2.SingleK.Companion
import arrow.fx.rx2.extensions.SingleKFunctor
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
internal val functor_singleton: SingleKFunctor = object : arrow.fx.rx2.extensions.SingleKFunctor {}

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForSingleK, A>.map(arg1: Function1<A, B>): SingleK<B> =
    arrow.fx.rx2.SingleK.functor().run {
  this@map.map<A, B>(arg1) as arrow.fx.rx2.SingleK<B>
}

@JvmName("imap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForSingleK, A>.imap(arg1: Function1<A, B>, arg2: Function1<B, A>): SingleK<B> =
    arrow.fx.rx2.SingleK.functor().run {
  this@imap.imap<A, B>(arg1, arg2) as arrow.fx.rx2.SingleK<B>
}

@JvmName("lift")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> lift(arg0: Function1<A, B>): Function1<Kind<ForSingleK, A>, Kind<ForSingleK, B>> =
    arrow.fx.rx2.SingleK
   .functor()
   .lift<A, B>(arg0) as kotlin.Function1<arrow.Kind<arrow.fx.rx2.ForSingleK, A>,
    arrow.Kind<arrow.fx.rx2.ForSingleK, B>>

@JvmName("void")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> Kind<ForSingleK, A>.void(): SingleK<Unit> = arrow.fx.rx2.SingleK.functor().run {
  this@void.void<A>() as arrow.fx.rx2.SingleK<kotlin.Unit>
}

@JvmName("fproduct")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForSingleK, A>.fproduct(arg1: Function1<A, B>): SingleK<Tuple2<A, B>> =
    arrow.fx.rx2.SingleK.functor().run {
  this@fproduct.fproduct<A, B>(arg1) as arrow.fx.rx2.SingleK<arrow.core.Tuple2<A, B>>
}

@JvmName("mapConst")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForSingleK, A>.mapConst(arg1: B): SingleK<B> = arrow.fx.rx2.SingleK.functor().run {
  this@mapConst.mapConst<A, B>(arg1) as arrow.fx.rx2.SingleK<B>
}

@JvmName("mapConst")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> A.mapConst(arg1: Kind<ForSingleK, B>): SingleK<A> = arrow.fx.rx2.SingleK.functor().run {
  this@mapConst.mapConst<A, B>(arg1) as arrow.fx.rx2.SingleK<A>
}

@JvmName("tupleLeft")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForSingleK, A>.tupleLeft(arg1: B): SingleK<Tuple2<B, A>> =
    arrow.fx.rx2.SingleK.functor().run {
  this@tupleLeft.tupleLeft<A, B>(arg1) as arrow.fx.rx2.SingleK<arrow.core.Tuple2<B, A>>
}

@JvmName("tupleRight")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForSingleK, A>.tupleRight(arg1: B): SingleK<Tuple2<A, B>> =
    arrow.fx.rx2.SingleK.functor().run {
  this@tupleRight.tupleRight<A, B>(arg1) as arrow.fx.rx2.SingleK<arrow.core.Tuple2<A, B>>
}

@JvmName("widen")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <B, A : B> Kind<ForSingleK, A>.widen(): SingleK<B> = arrow.fx.rx2.SingleK.functor().run {
  this@widen.widen<B, A>() as arrow.fx.rx2.SingleK<B>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateRxJava)
inline fun Companion.functor(): SingleKFunctor = functor_singleton
