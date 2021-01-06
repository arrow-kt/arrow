package arrow.core.extensions.listk.functor

import arrow.Kind
import arrow.core.ForListK
import arrow.core.ListK
import arrow.core.ListK.Companion
import arrow.core.Tuple2
import arrow.core.extensions.ListKFunctor
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.Unit
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val functor_singleton: ListKFunctor = object : arrow.core.extensions.ListKFunctor {}

/**
 *  Transform the [F] wrapped value [A] into [B] preserving the [F] structure
 *  Kind<F, A> -> Kind<F, B>
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.*
 * import arrow.core.extensions.listk.functor.*
 * import arrow.core.*
 *
 *
 *  import arrow.core.extensions.listk.applicative.just
 *
 *  fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   "Hello".just().map({ "$it World" })
 *   //sampleEnd
 *   println(result)
 *  }
 *  ```
 */
@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("map(arg1)"))
fun <A, B> Kind<ForListK, A>.map(arg1: Function1<A, B>): ListK<B> = arrow.core.ListK.functor().run {
  this@map.map<A, B>(arg1) as arrow.core.ListK<B>
}

@JvmName("imap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("map(arg1)"))
fun <A, B> Kind<ForListK, A>.imap(arg1: Function1<A, B>, arg2: Function1<B, A>): ListK<B> =
    arrow.core.ListK.functor().run {
  this@imap.imap<A, B>(arg1, arg2) as arrow.core.ListK<B>
}

/**
 *  Lifts a function `A -> B` to the [F] structure returning a polymorphic function
 *  that can be applied over all [F] values in the shape of Kind<F, A>
 *
 *  `A -> B -> Kind<F, A> -> Kind<F, B>`
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.*
 * import arrow.core.extensions.listk.functor.*
 * import arrow.core.*
 *
 *
 *  import arrow.core.extensions.listk.applicative.just
 *
 *  fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   lift({ s: CharSequence -> "$s World" })("Hello".just())
 *   //sampleEnd
 *   println(result)
 *  }
 *  ```
 */
@JvmName("lift")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("{ l: List<A> -> l.map(arg0) }"))
fun <A, B> lift(arg0: Function1<A, B>): Function1<Kind<ForListK, A>, Kind<ForListK, B>> =
    arrow.core.ListK
   .functor()
   .lift<A, B>(arg0) as kotlin.Function1<arrow.Kind<arrow.core.ForListK, A>,
    arrow.Kind<arrow.core.ForListK, B>>

@JvmName("void")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("void()", "arrow.core.void"))
fun <A> Kind<ForListK, A>.void(): ListK<Unit> = arrow.core.ListK.functor().run {
  this@void.void<A>() as arrow.core.ListK<kotlin.Unit>
}

@JvmName("fproduct")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("fproduct(arg1)", "arrow.core.fproduct"))
fun <A, B> Kind<ForListK, A>.fproduct(arg1: Function1<A, B>): ListK<Tuple2<A, B>> =
    arrow.core.ListK.functor().run {
  this@fproduct.fproduct<A, B>(arg1) as arrow.core.ListK<arrow.core.Tuple2<A, B>>
}

@JvmName("mapConst")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("mapConst(arg1)", "arrow.core.mapConst"))
fun <A, B> Kind<ForListK, A>.mapConst(arg1: B): ListK<B> = arrow.core.ListK.functor().run {
  this@mapConst.mapConst<A, B>(arg1) as arrow.core.ListK<B>
}

/**
 *  Replaces the [B] value inside [F] with [A] resulting in a Kind<F, A>
 */
@JvmName("mapConst")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("arg1.mapConst(this)", "arrow.core.mapConst"))
fun <A, B> A.mapConst(arg1: Kind<ForListK, B>): ListK<A> = arrow.core.ListK.functor().run {
  this@mapConst.mapConst<A, B>(arg1) as arrow.core.ListK<A>
}

@JvmName("tupleLeft")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("tupleLeft(arg1)", "arrow.core.tupleLeft"))
fun <A, B> Kind<ForListK, A>.tupleLeft(arg1: B): ListK<Tuple2<B, A>> =
    arrow.core.ListK.functor().run {
  this@tupleLeft.tupleLeft<A, B>(arg1) as arrow.core.ListK<arrow.core.Tuple2<B, A>>
}

@JvmName("tupleRight")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("tupleRight(arg1)", "arrow.core.tupleRight"))
fun <A, B> Kind<ForListK, A>.tupleRight(arg1: B): ListK<Tuple2<A, B>> =
    arrow.core.ListK.functor().run {
  this@tupleRight.tupleRight<A, B>(arg1) as arrow.core.ListK<arrow.core.Tuple2<A, B>>
}

@JvmName("widen")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("widen()", "arrow.core.widen"))
fun <B, A : B> Kind<ForListK, A>.widen(): ListK<B> = arrow.core.ListK.functor().run {
  this@widen.widen<B, A>() as arrow.core.ListK<B>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("Functor typeclasses is deprecated. Use concrete methods on List")
inline fun Companion.functor(): ListKFunctor = functor_singleton
