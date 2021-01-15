package arrow.core.extensions.map.functor

import arrow.Kind
import arrow.core.ForMapK
import arrow.core.Tuple2
import arrow.core.extensions.MapKFunctor
import kotlin.Any
import kotlin.Deprecated
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.Map
import kotlin.jvm.JvmName

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
fun <K, A, B> Map<K, A>.map(arg1: Function1<A, B>): Map<K, B> =
    arrow.core.extensions.map.functor.Map.functor<K>().run {
  arrow.core.MapK(this@map).map<A, B>(arg1) as kotlin.collections.Map<K, B>
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
fun <K, A, B> Map<K, A>.imap(arg1: Function1<A, B>, arg2: Function1<B, A>): Map<K, B> =
    arrow.core.extensions.map.functor.Map.functor<K>().run {
  arrow.core.MapK(this@imap).imap<A, B>(arg1, arg2) as kotlin.collections.Map<K, B>
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
    K>, B>> = arrow.core.extensions.map.functor.Map
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
fun <K, A> Map<K, A>.void(): Map<K, Unit> = arrow.core.extensions.map.functor.Map.functor<K>().run {
  arrow.core.MapK(this@void).void<A>() as kotlin.collections.Map<K, kotlin.Unit>
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
fun <K, A, B> Map<K, A>.fproduct(arg1: Function1<A, B>): Map<K, Tuple2<A, B>> =
    arrow.core.extensions.map.functor.Map.functor<K>().run {
  arrow.core.MapK(this@fproduct).fproduct<A, B>(arg1) as kotlin.collections.Map<K,
    arrow.core.Tuple2<A, B>>
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
fun <K, A, B> Map<K, A>.mapConst(arg1: B): Map<K, B> =
    arrow.core.extensions.map.functor.Map.functor<K>().run {
  arrow.core.MapK(this@mapConst).mapConst<A, B>(arg1) as kotlin.collections.Map<K, B>
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
fun <K, A, B> A.mapConst(arg1: Map<K, B>): Map<K, A> =
    arrow.core.extensions.map.functor.Map.functor<K>().run {
  this@mapConst.mapConst<A, B>(arrow.core.MapK(arg1)) as kotlin.collections.Map<K, A>
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
fun <K, A, B> Map<K, A>.tupleLeft(arg1: B): Map<K, Tuple2<B, A>> =
    arrow.core.extensions.map.functor.Map.functor<K>().run {
  arrow.core.MapK(this@tupleLeft).tupleLeft<A, B>(arg1) as kotlin.collections.Map<K,
    arrow.core.Tuple2<B, A>>
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
fun <K, A, B> Map<K, A>.tupleRight(arg1: B): Map<K, Tuple2<A, B>> =
    arrow.core.extensions.map.functor.Map.functor<K>().run {
  arrow.core.MapK(this@tupleRight).tupleRight<A, B>(arg1) as kotlin.collections.Map<K,
    arrow.core.Tuple2<A, B>>
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
fun <K, B, A : B> Map<K, A>.widen(): Map<K, B> =
    arrow.core.extensions.map.functor.Map.functor<K>().run {
  arrow.core.MapK(this@widen).widen<B, A>() as kotlin.collections.Map<K, B>
}

/**
 * cached extension
 */
@PublishedApi()
internal val functor_singleton: MapKFunctor<Any?> = object : MapKFunctor<Any?> {}

object Map {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated("Functor typeclasses is deprecated. Use concrete methods on Map")
  inline fun <K> functor(): MapKFunctor<K> = functor_singleton as
      arrow.core.extensions.MapKFunctor<K>}
