package arrow.core.extensions.list.functor

import arrow.Kind
import arrow.core.ForListK
import arrow.core.void as _void
import arrow.core.fproduct as _fproduct
import arrow.core.mapConst as _mapConst
import arrow.core.tupleLeft as _tupleLeft
import arrow.core.tupleRight as _tupleRight
import arrow.core.widen as _widen
import arrow.core.Tuple2
import arrow.core.extensions.ListKFunctor
import kotlin.Function1
import kotlin.collections.map as _map
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.jvm.JvmName

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
fun <A, B> List<A>.map(arg1: Function1<A, B>): List<B> =
  _map(arg1)

@JvmName("imap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("map(arg1)"))
fun <A, B> List<A>.imap(arg1: Function1<A, B>, arg2: Function1<B, A>): List<B> =
  _map(arg1)

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
    arrow.core.extensions.list.functor.List
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
fun <A> List<A>.void(): List<Unit> =
  _void()

@JvmName("fproduct")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("fproduct(arg1)", "arrow.core.fproduct"))
fun <A, B> List<A>.fproduct(arg1: Function1<A, B>): List<Tuple2<A, B>> =
  _fproduct(arg1)

@JvmName("mapConst")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("mapConst(arg1)", "arrow.core.mapConst"))
fun <A, B> List<A>.mapConst(arg1: B): List<B> =
  _mapConst(arg1)

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
fun <A, B> A.mapConst(arg1: List<B>): List<A> =
  arg1._mapConst(this)

@JvmName("tupleLeft")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("tupleLeft(arg1)", "arrow.core.tupleLeft"))
fun <A, B> List<A>.tupleLeft(arg1: B): List<Tuple2<B, A>> =
  _tupleLeft(arg1)

@JvmName("tupleRight")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("tupleRight(arg1)", "arrow.core.tupleRight"))
fun <A, B> List<A>.tupleRight(arg1: B): List<Tuple2<A, B>> =
  _tupleRight(arg1)

@JvmName("widen")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("widen()", "arrow.core.widen"))
fun <B, A : B> List<A>.widen(): List<B> =
  _widen()

/**
 * cached extension
 */
@PublishedApi()
internal val functor_singleton: ListKFunctor = object : arrow.core.extensions.ListKFunctor {}

@Deprecated("Receiver List object is deprecated, prefer to turn List functions into top-level functions")
object List {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated("Functor typeclasses is deprecated. Use concrete methods on List")
  inline fun functor(): ListKFunctor = functor_singleton}
