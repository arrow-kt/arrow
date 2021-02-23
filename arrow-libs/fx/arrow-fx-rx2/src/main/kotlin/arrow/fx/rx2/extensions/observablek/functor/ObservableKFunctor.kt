package arrow.fx.rx2.extensions.observablek.functor

import arrow.Kind
import arrow.core.Tuple2
import arrow.fx.rx2.DeprecateRxJava
import arrow.fx.rx2.ForObservableK
import arrow.fx.rx2.ObservableK
import arrow.fx.rx2.ObservableK.Companion
import arrow.fx.rx2.extensions.ObservableKFunctor
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
internal val functor_singleton: ObservableKFunctor = object :
  arrow.fx.rx2.extensions.ObservableKFunctor {}

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForObservableK, A>.map(arg1: Function1<A, B>): ObservableK<B> =
  arrow.fx.rx2.ObservableK.functor().run {
    this@map.map<A, B>(arg1) as arrow.fx.rx2.ObservableK<B>
  }

@JvmName("imap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForObservableK, A>.imap(arg1: Function1<A, B>, arg2: Function1<B, A>):
  ObservableK<B> = arrow.fx.rx2.ObservableK.functor().run {
    this@imap.imap<A, B>(arg1, arg2) as arrow.fx.rx2.ObservableK<B>
  }

@JvmName("lift")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> lift(arg0: Function1<A, B>): Function1<Kind<ForObservableK, A>, Kind<ForObservableK, B>> =
  arrow.fx.rx2.ObservableK
    .functor()
    .lift<A, B>(arg0) as kotlin.Function1<arrow.Kind<arrow.fx.rx2.ForObservableK, A>,
    arrow.Kind<arrow.fx.rx2.ForObservableK, B>>

@JvmName("void")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> Kind<ForObservableK, A>.void(): ObservableK<Unit> = arrow.fx.rx2.ObservableK.functor().run {
  this@void.void<A>() as arrow.fx.rx2.ObservableK<kotlin.Unit>
}

@JvmName("fproduct")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForObservableK, A>.fproduct(arg1: Function1<A, B>): ObservableK<Tuple2<A, B>> =
  arrow.fx.rx2.ObservableK.functor().run {
    this@fproduct.fproduct<A, B>(arg1) as arrow.fx.rx2.ObservableK<arrow.core.Tuple2<A, B>>
  }

@JvmName("mapConst")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForObservableK, A>.mapConst(arg1: B): ObservableK<B> =
  arrow.fx.rx2.ObservableK.functor().run {
    this@mapConst.mapConst<A, B>(arg1) as arrow.fx.rx2.ObservableK<B>
  }

@JvmName("mapConst")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> A.mapConst(arg1: Kind<ForObservableK, B>): ObservableK<A> =
  arrow.fx.rx2.ObservableK.functor().run {
    this@mapConst.mapConst<A, B>(arg1) as arrow.fx.rx2.ObservableK<A>
  }

@JvmName("tupleLeft")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForObservableK, A>.tupleLeft(arg1: B): ObservableK<Tuple2<B, A>> =
  arrow.fx.rx2.ObservableK.functor().run {
    this@tupleLeft.tupleLeft<A, B>(arg1) as arrow.fx.rx2.ObservableK<arrow.core.Tuple2<B, A>>
  }

@JvmName("tupleRight")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForObservableK, A>.tupleRight(arg1: B): ObservableK<Tuple2<A, B>> =
  arrow.fx.rx2.ObservableK.functor().run {
    this@tupleRight.tupleRight<A, B>(arg1) as arrow.fx.rx2.ObservableK<arrow.core.Tuple2<A, B>>
  }

@JvmName("widen")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <B, A : B> Kind<ForObservableK, A>.widen(): ObservableK<B> =
  arrow.fx.rx2.ObservableK.functor().run {
    this@widen.widen<B, A>() as arrow.fx.rx2.ObservableK<B>
  }

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateRxJava)
inline fun Companion.functor(): ObservableKFunctor = functor_singleton
