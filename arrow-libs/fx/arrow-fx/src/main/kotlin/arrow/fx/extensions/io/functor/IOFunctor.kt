package arrow.fx.extensions.io.functor

import arrow.Kind
import arrow.core.Tuple2
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.IO.Companion
import arrow.fx.IODeprecation
import arrow.fx.extensions.IOFunctor
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
internal val functor_singleton: IOFunctor = object : arrow.fx.extensions.IOFunctor {}

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B> Kind<ForIO, A>.map(arg1: Function1<A, B>): IO<B> = arrow.fx.IO.functor().run {
  this@map.map<A, B>(arg1) as arrow.fx.IO<B>
}

@JvmName("imap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B> Kind<ForIO, A>.imap(arg1: Function1<A, B>, arg2: Function1<B, A>): IO<B> =
  arrow.fx.IO.functor().run {
    this@imap.imap<A, B>(arg1, arg2) as arrow.fx.IO<B>
  }

@JvmName("lift")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B> lift(arg0: Function1<A, B>): Function1<Kind<ForIO, A>, Kind<ForIO, B>> = arrow.fx.IO
  .functor()
  .lift<A, B>(arg0) as kotlin.Function1<arrow.Kind<arrow.fx.ForIO, A>, arrow.Kind<arrow.fx.ForIO,
    B>>

@JvmName("void")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A> Kind<ForIO, A>.void(): IO<Unit> = arrow.fx.IO.functor().run {
  this@void.void<A>() as arrow.fx.IO<kotlin.Unit>
}

@JvmName("fproduct")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B> Kind<ForIO, A>.fproduct(arg1: Function1<A, B>): IO<Tuple2<A, B>> =
  arrow.fx.IO.functor().run {
    this@fproduct.fproduct<A, B>(arg1) as arrow.fx.IO<arrow.core.Tuple2<A, B>>
  }

@JvmName("mapConst")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B> Kind<ForIO, A>.mapConst(arg1: B): IO<B> = arrow.fx.IO.functor().run {
  this@mapConst.mapConst<A, B>(arg1) as arrow.fx.IO<B>
}

@JvmName("mapConst")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B> A.mapConst(arg1: Kind<ForIO, B>): IO<A> = arrow.fx.IO.functor().run {
  this@mapConst.mapConst<A, B>(arg1) as arrow.fx.IO<A>
}

@JvmName("tupleLeft")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B> Kind<ForIO, A>.tupleLeft(arg1: B): IO<Tuple2<B, A>> = arrow.fx.IO.functor().run {
  this@tupleLeft.tupleLeft<A, B>(arg1) as arrow.fx.IO<arrow.core.Tuple2<B, A>>
}

@JvmName("tupleRight")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B> Kind<ForIO, A>.tupleRight(arg1: B): IO<Tuple2<A, B>> = arrow.fx.IO.functor().run {
  this@tupleRight.tupleRight<A, B>(arg1) as arrow.fx.IO<arrow.core.Tuple2<A, B>>
}

@JvmName("widen")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <B, A : B> Kind<ForIO, A>.widen(): IO<B> = arrow.fx.IO.functor().run {
  this@widen.widen<B, A>() as arrow.fx.IO<B>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(IODeprecation)
inline fun Companion.functor(): IOFunctor = functor_singleton
