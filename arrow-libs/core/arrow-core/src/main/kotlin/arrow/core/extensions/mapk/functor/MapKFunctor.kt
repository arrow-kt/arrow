package arrow.core.extensions.mapk.functor

import arrow.Kind
import arrow.core.ForMapK
import arrow.core.MapK
import arrow.core.MapK.Companion
import arrow.core.Tuple2
import arrow.core.extensions.MapKFunctor
import kotlin.Any
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
internal val functor_singleton: MapKFunctor<Any?> = object : MapKFunctor<Any?> {}

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith("mapValues { (_, a) -> arg1(a) }"),
  DeprecationLevel.WARNING
)
fun <K, A, B> Kind<Kind<ForMapK, K>, A>.map(arg1: Function1<A, B>): MapK<K, B> =
  arrow.core.MapK.functor<K>().run {
    this@map.map<A, B>(arg1) as arrow.core.MapK<K, B>
  }

@JvmName("imap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith("mapValues { (_, a) -> arg1(a) }"),
  DeprecationLevel.WARNING
)
fun <K, A, B> Kind<Kind<ForMapK, K>, A>.imap(arg1: Function1<A, B>, arg2: Function1<B, A>): MapK<K,
  B> = arrow.core.MapK.functor<K>().run {
  this@imap.imap<A, B>(arg1, arg2) as arrow.core.MapK<K, B>
}

@JvmName("lift")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("{ l: Map<K, A> -> l.mapValues { (_, a) -> arg0(a) } }"))
fun <K, A, B> lift(arg0: Function1<A, B>): Function1<Kind<Kind<ForMapK, K>, A>, Kind<Kind<ForMapK,
      K>, B>> = arrow.core.MapK
  .functor<K>()
  .lift<A, B>(arg0) as kotlin.Function1<arrow.Kind<arrow.Kind<arrow.core.ForMapK, K>, A>,
  arrow.Kind<arrow.Kind<arrow.core.ForMapK, K>, B>>

@JvmName("void")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "void()",
    "arrow.core.void"
  ),
  DeprecationLevel.WARNING
)
fun <K, A> Kind<Kind<ForMapK, K>, A>.void(): MapK<K, Unit> = arrow.core.MapK.functor<K>().run {
  this@void.void<A>() as arrow.core.MapK<K, kotlin.Unit>
}

@JvmName("fproduct")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "fproduct(arg1)",
    "arrow.core.fproduct"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B> Kind<Kind<ForMapK, K>, A>.fproduct(arg1: Function1<A, B>): MapK<K, Tuple2<A, B>> =
  arrow.core.MapK.functor<K>().run {
    this@fproduct.fproduct<A, B>(arg1) as arrow.core.MapK<K, arrow.core.Tuple2<A, B>>
  }

@JvmName("mapConst")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith("mapValues { arg1 }"),
  DeprecationLevel.WARNING
)
fun <K, A, B> Kind<Kind<ForMapK, K>, A>.mapConst(arg1: B): MapK<K, B> =
  arrow.core.MapK.functor<K>().run {
    this@mapConst.mapConst<A, B>(arg1) as arrow.core.MapK<K, B>
  }

@JvmName("mapConst")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith("arg1.mapValues { this }"),
  DeprecationLevel.WARNING
)
fun <K, A, B> A.mapConst(arg1: Kind<Kind<ForMapK, K>, B>): MapK<K, A> =
  arrow.core.MapK.functor<K>().run {
    this@mapConst.mapConst<A, B>(arg1) as arrow.core.MapK<K, A>
  }

@JvmName("tupleLeft")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "tupleLeft(arg1)",
    "arrow.core.tupleLeft"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B> Kind<Kind<ForMapK, K>, A>.tupleLeft(arg1: B): MapK<K, Tuple2<B, A>> =
  arrow.core.MapK.functor<K>().run {
    this@tupleLeft.tupleLeft<A, B>(arg1) as arrow.core.MapK<K, arrow.core.Tuple2<B, A>>
  }

@JvmName("tupleRight")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "tupleRight(arg1)",
    "arrow.core.tupleRight"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B> Kind<Kind<ForMapK, K>, A>.tupleRight(arg1: B): MapK<K, Tuple2<A, B>> =
  arrow.core.MapK.functor<K>().run {
    this@tupleRight.tupleRight<A, B>(arg1) as arrow.core.MapK<K, arrow.core.Tuple2<A, B>>
  }

@JvmName("widen")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "widen()",
    "arrow.core.widen"
  ),
  DeprecationLevel.WARNING
)
fun <K, B, A : B> Kind<Kind<ForMapK, K>, A>.widen(): MapK<K, B> = arrow.core.MapK.functor<K>().run {
  this@widen.widen<B, A>() as arrow.core.MapK<K, B>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("Functor typeclasses is deprecated. Use concrete methods on Map")
inline fun <K> Companion.functor(): MapKFunctor<K> = functor_singleton as
  arrow.core.extensions.MapKFunctor<K>
