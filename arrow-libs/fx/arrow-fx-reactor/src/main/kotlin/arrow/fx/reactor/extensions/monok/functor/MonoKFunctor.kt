package arrow.fx.reactor.extensions.monok.functor

import arrow.Kind
import arrow.core.Tuple2
import arrow.fx.reactor.DeprecateReactor
import arrow.fx.reactor.ForMonoK
import arrow.fx.reactor.MonoK
import arrow.fx.reactor.MonoK.Companion
import arrow.fx.reactor.extensions.MonoKFunctor
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
internal val functor_singleton: MonoKFunctor = object : arrow.fx.reactor.extensions.MonoKFunctor {}

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A, B> Kind<ForMonoK, A>.map(arg1: Function1<A, B>): MonoK<B> =
    arrow.fx.reactor.MonoK.functor().run {
  this@map.map<A, B>(arg1) as arrow.fx.reactor.MonoK<B>
}

@JvmName("imap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A, B> Kind<ForMonoK, A>.imap(arg1: Function1<A, B>, arg2: Function1<B, A>): MonoK<B> =
    arrow.fx.reactor.MonoK.functor().run {
  this@imap.imap<A, B>(arg1, arg2) as arrow.fx.reactor.MonoK<B>
}

@JvmName("lift")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A, B> lift(arg0: Function1<A, B>): Function1<Kind<ForMonoK, A>, Kind<ForMonoK, B>> =
    arrow.fx.reactor.MonoK
   .functor()
   .lift<A, B>(arg0) as kotlin.Function1<arrow.Kind<arrow.fx.reactor.ForMonoK, A>,
    arrow.Kind<arrow.fx.reactor.ForMonoK, B>>

@JvmName("void")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A> Kind<ForMonoK, A>.void(): MonoK<Unit> = arrow.fx.reactor.MonoK.functor().run {
  this@void.void<A>() as arrow.fx.reactor.MonoK<kotlin.Unit>
}

@JvmName("fproduct")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A, B> Kind<ForMonoK, A>.fproduct(arg1: Function1<A, B>): MonoK<Tuple2<A, B>> =
    arrow.fx.reactor.MonoK.functor().run {
  this@fproduct.fproduct<A, B>(arg1) as arrow.fx.reactor.MonoK<arrow.core.Tuple2<A, B>>
}

@JvmName("mapConst")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A, B> Kind<ForMonoK, A>.mapConst(arg1: B): MonoK<B> = arrow.fx.reactor.MonoK.functor().run {
  this@mapConst.mapConst<A, B>(arg1) as arrow.fx.reactor.MonoK<B>
}

@JvmName("mapConst")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A, B> A.mapConst(arg1: Kind<ForMonoK, B>): MonoK<A> = arrow.fx.reactor.MonoK.functor().run {
  this@mapConst.mapConst<A, B>(arg1) as arrow.fx.reactor.MonoK<A>
}

@JvmName("tupleLeft")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A, B> Kind<ForMonoK, A>.tupleLeft(arg1: B): MonoK<Tuple2<B, A>> =
    arrow.fx.reactor.MonoK.functor().run {
  this@tupleLeft.tupleLeft<A, B>(arg1) as arrow.fx.reactor.MonoK<arrow.core.Tuple2<B, A>>
}

@JvmName("tupleRight")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A, B> Kind<ForMonoK, A>.tupleRight(arg1: B): MonoK<Tuple2<A, B>> =
    arrow.fx.reactor.MonoK.functor().run {
  this@tupleRight.tupleRight<A, B>(arg1) as arrow.fx.reactor.MonoK<arrow.core.Tuple2<A, B>>
}

@JvmName("widen")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <B, A : B> Kind<ForMonoK, A>.widen(): MonoK<B> = arrow.fx.reactor.MonoK.functor().run {
  this@widen.widen<B, A>() as arrow.fx.reactor.MonoK<B>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateReactor)
inline fun Companion.functor(): MonoKFunctor = functor_singleton
