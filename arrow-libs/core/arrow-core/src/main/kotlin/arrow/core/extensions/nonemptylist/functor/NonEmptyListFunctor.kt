package arrow.core.extensions.nonemptylist.functor

import arrow.Kind
import arrow.core.ForNonEmptyList
import arrow.core.NonEmptyList
import arrow.core.NonEmptyList.Companion
import arrow.core.Tuple2
import arrow.core.extensions.NonEmptyListFunctor

/**
 * cached extension
 */
@PublishedApi()
internal val functor_singleton: NonEmptyListFunctor = object :
    arrow.core.extensions.NonEmptyListFunctor {}

/**
 *  Transform the [F] wrapped value [A] into [B] preserving the [F] structure
 *  Kind<F, A> -> Kind<F, B>
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.*
 * import arrow.core.extensions.nonemptylist.functor.*
 * import arrow.core.*
 *
 *
 *  import arrow.core.extensions.nonemptylist.applicative.just
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
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "fix().map<B>(arg1)",
    "arrow.core.fix"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForNonEmptyList, A>.map(arg1: Function1<A, B>): NonEmptyList<B> =
    arrow.core.NonEmptyList.functor().run {
  this@map.map<A, B>(arg1) as arrow.core.NonEmptyList<B>
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
  ReplaceWith(
    "fix().map<B>(arg1)",
    "arrow.core.fix"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForNonEmptyList, A>.imap(arg1: Function1<A, B>, arg2: Function1<B, A>):
    NonEmptyList<B> = arrow.core.NonEmptyList.functor().run {
  this@imap.imap<A, B>(arg1, arg2) as arrow.core.NonEmptyList<B>
}

/**
 *  Lifts a function `A -> B` to the [F] structure returning a polymorphic function
 *  that can be applied over all [F] values in the shape of Kind<F, A>
 *
 *  `A -> B -> Kind<F, A> -> Kind<F, B>`
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.*
 * import arrow.core.extensions.nonemptylist.functor.*
 * import arrow.core.*
 *
 *
 *  import arrow.core.extensions.nonemptylist.applicative.just
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
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "{ nel: NonEmptyList<Int> -> nel.map { arg0 }}",
    "arrow.core.NonEmptyList"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> lift(arg0: Function1<A, B>): Function1<Kind<ForNonEmptyList, A>, Kind<ForNonEmptyList,
    B>> = arrow.core.NonEmptyList
   .functor()
   .lift<A, B>(arg0) as kotlin.Function1<arrow.Kind<arrow.core.ForNonEmptyList, A>,
    arrow.Kind<arrow.core.ForNonEmptyList, B>>

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
    "fix().void<A>()",
    "arrow.core.fix", "arrow.core.void"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForNonEmptyList, A>.void(): NonEmptyList<Unit> =
    arrow.core.NonEmptyList.functor().run {
  this@void.void<A>() as arrow.core.NonEmptyList<kotlin.Unit>
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
    "fix().fproduct<A, B>(arg1)",
    "arrow.core.fix", "arrow.core.fproduct"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForNonEmptyList, A>.fproduct(arg1: Function1<A, B>): NonEmptyList<Tuple2<A, B>> =
    arrow.core.NonEmptyList.functor().run {
  this@fproduct.fproduct<A, B>(arg1) as arrow.core.NonEmptyList<arrow.core.Tuple2<A, B>>
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
  ReplaceWith(
    "fix().mapConst<A, B>(arg1)",
    "arrow.core.fix", "arrow.core.mapConst"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForNonEmptyList, A>.mapConst(arg1: B): NonEmptyList<B> =
    arrow.core.NonEmptyList.functor().run {
  this@mapConst.mapConst<A, B>(arg1) as arrow.core.NonEmptyList<B>
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
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "arg1.fix().mapConst<B, A>(this)",
    "arrow.core.fix", "arrow.core.mapConst"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> A.mapConst(arg1: Kind<ForNonEmptyList, B>): NonEmptyList<A> =
    arrow.core.NonEmptyList.functor().run {
  this@mapConst.mapConst<A, B>(arg1) as arrow.core.NonEmptyList<A>
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
    "fix().tupleLeft<A, B>(arg1)",
    "arrow.core.fix", "arrow.core.tupleLeft"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForNonEmptyList, A>.tupleLeft(arg1: B): NonEmptyList<Tuple2<B, A>> =
    arrow.core.NonEmptyList.functor().run {
  this@tupleLeft.tupleLeft<A, B>(arg1) as arrow.core.NonEmptyList<arrow.core.Tuple2<B, A>>
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
    "fix().tupleRight<A, B>(arg1)",
    "arrow.core.fix", "arrow.core.tupleRight"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForNonEmptyList, A>.tupleRight(arg1: B): NonEmptyList<Tuple2<A, B>> =
    arrow.core.NonEmptyList.functor().run {
  this@tupleRight.tupleRight<A, B>(arg1) as arrow.core.NonEmptyList<arrow.core.Tuple2<A, B>>
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
    "fix().widen<B, A>()",
    "arrow.core.fix", "arrow.core.widen"
  ),
  DeprecationLevel.WARNING
)
fun <B, A : B> Kind<ForNonEmptyList, A>.widen(): NonEmptyList<B> =
    arrow.core.NonEmptyList.functor().run {
  this@widen.widen<B, A>() as arrow.core.NonEmptyList<B>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("Functor typeclass is deprecated. Use concrete methods on NonEmptyList")
inline fun Companion.functor(): NonEmptyListFunctor = functor_singleton as arrow.core.extensions.NonEmptyListFunctor
