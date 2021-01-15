package arrow.core.extensions.const.functor

import arrow.Kind
import arrow.core.Const
import arrow.core.Const.Companion
import arrow.core.ForConst
import arrow.core.Tuple2
import arrow.core.extensions.ConstFunctor
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
internal val functor_singleton: ConstFunctor<Any?> = object : ConstFunctor<Any?> {}

/**
 *  Transform the [F] wrapped value [A] into [B] preserving the [F] structure
 *  Kind<F, A> -> Kind<F, B>
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.*
 * import arrow.core.extensions.const.functor.*
 * import arrow.core.*
 *
 *
 *  import arrow.core.extensions.const.applicative.just
 * import arrow.core.extensions.monoid
 *
 *  fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   "Hello".just<String>(String.monoid()).map<String, String>({ "$it World" })
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
  "map(arg1)",
  "arrow.core.map"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<Kind<ForConst, A>, A>.map(arg1: Function1<A, B>): Const<A, B> =
    arrow.core.Const.functor<A>().run {
  this@map.map(arg1) as arrow.core.Const<A, B>
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
  "imap(arg1, arg2)",
  "arrow.core.imap"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<Kind<ForConst, A>, A>.imap(arg1: Function1<A, B>, arg2: Function1<B, A>): Const<A,
    B> = arrow.core.Const.functor<A>().run {
  this@imap.imap(arg1, arg2) as arrow.core.Const<A, B>
}

/**
 *  Lifts a function `A -> B` to the [F] structure returning a polymorphic function
 *  that can be applied over all [F] values in the shape of Kind<F, A>
 *
 *  `A -> B -> Kind<F, A> -> Kind<F, B>`
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.*
 * import arrow.core.extensions.const.functor.*
 * import arrow.core.*
 *
 *
 *  import arrow.core.extensions.const.applicative.just
 * import arrow.core.extensions.monoid
 *
 *  fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   lift<String, String>({ s: CharSequence -> "$s World" })("Hello".just<String>(String.monoid()))
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
  "lift(arg0)",
  "arrow.core.Const.lift"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> lift(arg0: Function1<A, B>): Function1<Kind<Kind<ForConst, A>, A>, Kind<Kind<ForConst,
    A>, B>> = arrow.core.Const
   .functor<A>()
   .lift(arg0) as kotlin.Function1<arrow.Kind<arrow.Kind<arrow.core.ForConst, A>, A>,
    arrow.Kind<arrow.Kind<arrow.core.ForConst, A>, B>>

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
fun <A> Kind<Kind<ForConst, A>, A>.void(): Const<A, Unit> = arrow.core.Const.functor<A>().run {
  this@void.void() as arrow.core.Const<A, kotlin.Unit>
}

/**
 *  Applies [f] to an [A] inside [F] and returns the [F] structure with a tuple of the [A] value and the
 *  computed [B] value as result of applying [f]
 *
 *  Kind<F, A> -> Kind<F, Tuple2<A, B>>
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.*
 * import arrow.core.extensions.const.functor.*
 * import arrow.core.*
 *
 *
 *  import arrow.core.extensions.const.applicative.just
 * import arrow.core.extensions.monoid
 *
 *  fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   "Hello".just<String>(String.monoid()).fproduct<String, String>({ "$it World" })
 *   //sampleEnd
 *   println(result)
 *  }
 *  ```
 */
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
fun <A, B> Kind<Kind<ForConst, A>, A>.fproduct(arg1: Function1<A, B>): Const<A, Tuple2<A, B>> =
    arrow.core.Const.functor<A>().run {
  this@fproduct.fproduct(arg1) as arrow.core.Const<A, arrow.core.Tuple2<A, B>>
}

/**
 *  Replaces [A] inside [F] with [B] resulting in a Kind<F, B>
 *
 *  Kind<F, A> -> Kind<F, B>
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.*
 * import arrow.core.extensions.const.functor.*
 * import arrow.core.*
 *
 *
 *  import arrow.core.extensions.const.applicative.just
 * import arrow.core.extensions.monoid
 *
 *  fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   "Hello World".just<String>(String.monoid()).mapConst<String, String>("...")
 *   //sampleEnd
 *   println(result)
 *  }
 *  ```
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
  "mapConst(arg1)",
  "arrow.core.mapConst"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<Kind<ForConst, A>, A>.mapConst(arg1: B): Const<A, B> =
    arrow.core.Const.functor<A>().run {
  this@mapConst.mapConst(arg1) as arrow.core.Const<A, B>
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
  "mapConst(arg1)",
  "arrow.core.mapConst"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> A.mapConst(arg1: Kind<Kind<ForConst, A>, B>): Const<A, A> =
    arrow.core.Const.functor<A>().run {
  this@mapConst.mapConst(arg1) as arrow.core.Const<A, A>
}

/**
 *  Pairs [B] with [A] returning a Kind<F, Tuple2<B, A>>
 *
 *  Kind<F, A> -> Kind<F, Tuple2<B, A>>
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.*
 * import arrow.core.extensions.const.functor.*
 * import arrow.core.*
 *
 *
 *  import arrow.core.extensions.const.applicative.just
 * import arrow.core.extensions.monoid
 *
 *  fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   "Hello".just<String>(String.monoid()).tupleLeft<String, String>("World")
 *   //sampleEnd
 *   println(result)
 *  }
 *  ```
 */
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
fun <A, B> Kind<Kind<ForConst, A>, A>.tupleLeft(arg1: B): Const<A, Tuple2<B, A>> =
    arrow.core.Const.functor<A>().run {
  this@tupleLeft.tupleLeft(arg1) as arrow.core.Const<A, arrow.core.Tuple2<B, A>>
}

/**
 *  Pairs [A] with [B] returning a Kind<F, Tuple2<A, B>>
 *
 *  Kind<F, A> -> Kind<F, Tuple2<A, B>>
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.*
 * import arrow.core.extensions.const.functor.*
 * import arrow.core.*
 *
 *
 *  import arrow.core.extensions.const.applicative.just
 * import arrow.core.extensions.monoid
 *
 *  fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   "Hello".just<String>(String.monoid()).tupleRight<String, String>("World")
 *   //sampleEnd
 *   println(result)
 *  }
 *  ```
 */
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
fun <A, B> Kind<Kind<ForConst, A>, A>.tupleRight(arg1: B): Const<A, Tuple2<A, B>> =
    arrow.core.Const.functor<A>().run {
  this@tupleRight.tupleRight(arg1) as arrow.core.Const<A, arrow.core.Tuple2<A, B>>
}

/**
 *  Given [A] is a sub type of [B], re-type this value from Kind<F, A> to Kind<F, B>
 *
 *  Kind<F, A> -> Kind<F, B>
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.*
 * import arrow.core.extensions.const.functor.*
 * import arrow.core.*
 *
 *
 *  import arrow.core.extensions.const.applicative.just
 * import arrow.core.extensions.monoid
 *  import arrow.Kind
 *
 *  fun main(args: Array<String>) {
 *   val result: Kind<*, CharSequence> =
 *   //sampleStart
 *   "Hello".just<String>(String.monoid()).map<String, String>({ "$it World" }).widen<String,
 * String>()
 *   //sampleEnd
 *   println(result)
 *  }
 *  ```
 */
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
fun <A, B> Kind<Kind<ForConst, A>, A>.widen(): Const<A, B> = arrow.core.Const.functor<A>().run {
  this@widen.widen() as arrow.core.Const<A, B>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <A> Companion.functor(): ConstFunctor<A> = functor_singleton as
    arrow.core.extensions.ConstFunctor<A>
