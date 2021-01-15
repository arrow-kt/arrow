package arrow.core.extensions.andthen.functor

import arrow.Kind
import arrow.core.AndThen
import arrow.core.AndThen.Companion
import arrow.core.ForAndThen
import arrow.core.Tuple2
import arrow.core.extensions.AndThenFunctor
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
internal val functor_singleton: AndThenFunctor<Any?> = object : AndThenFunctor<Any?> {}

/**
 *  Transform the [F] wrapped value [A] into [B] preserving the [F] structure
 *  Kind<F, A> -> Kind<F, B>
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.*
 * import arrow.core.extensions.andthen.functor.*
 * import arrow.core.*
 *
 *
 *  import arrow.core.extensions.andthen.applicative.just
 *
 *  fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   "Hello".just<String, String>().map<String, String, String>({ "$it World" })
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
fun <X, A, B> Kind<Kind<ForAndThen, X>, A>.map(arg1: Function1<A, B>): AndThen<X, B> =
    arrow.core.AndThen.functor<X>().run {
  this@map.map<A, B>(arg1) as arrow.core.AndThen<X, B>
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
fun <X, A, B> Kind<Kind<ForAndThen, X>, A>.imap(arg1: Function1<A, B>, arg2: Function1<B, A>):
    AndThen<X, B> = arrow.core.AndThen.functor<X>().run {
  this@imap.imap<A, B>(arg1, arg2) as arrow.core.AndThen<X, B>
}

/**
 *  Lifts a function `A -> B` to the [F] structure returning a polymorphic function
 *  that can be applied over all [F] values in the shape of Kind<F, A>
 *
 *  `A -> B -> Kind<F, A> -> Kind<F, B>`
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.*
 * import arrow.core.extensions.andthen.functor.*
 * import arrow.core.*
 *
 *
 *  import arrow.core.extensions.andthen.applicative.just
 *
 *  fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   lift<String, String, String>({ s: CharSequence -> "$s World" })("Hello".just<String, String>())
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
  "arrow.core.AndThen.lift"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B> lift(arg0: Function1<A, B>): Function1<Kind<Kind<ForAndThen, X>, A>,
    Kind<Kind<ForAndThen, X>, B>> = arrow.core.AndThen
   .functor<X>()
   .lift<A, B>(arg0) as kotlin.Function1<arrow.Kind<arrow.Kind<arrow.core.ForAndThen, X>, A>,
    arrow.Kind<arrow.Kind<arrow.core.ForAndThen, X>, B>>

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
fun <X, A> Kind<Kind<ForAndThen, X>, A>.void(): AndThen<X, Unit> =
    arrow.core.AndThen.functor<X>().run {
  this@void.void<A>() as arrow.core.AndThen<X, kotlin.Unit>
}

/**
 *  Applies [f] to an [A] inside [F] and returns the [F] structure with a tuple of the [A] value and the
 *  computed [B] value as result of applying [f]
 *
 *  Kind<F, A> -> Kind<F, Tuple2<A, B>>
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.*
 * import arrow.core.extensions.andthen.functor.*
 * import arrow.core.*
 *
 *
 *  import arrow.core.extensions.andthen.applicative.just
 *
 *  fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   "Hello".just<String, String>().fproduct<String, String, String>({ "$it World" })
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
fun <X, A, B> Kind<Kind<ForAndThen, X>, A>.fproduct(arg1: Function1<A, B>): AndThen<X, Tuple2<A, B>> =
  arrow.core.AndThen.functor<X>().run {
    this@fproduct.fproduct<A, B>(arg1) as arrow.core.AndThen<X, arrow.core.Tuple2<A, B>>
  }

/**
 *  Replaces [A] inside [F] with [B] resulting in a Kind<F, B>
 *
 *  Kind<F, A> -> Kind<F, B>
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.*
 * import arrow.core.extensions.andthen.functor.*
 * import arrow.core.*
 *
 *
 *  import arrow.core.extensions.andthen.applicative.just
 *
 *  fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   "Hello World".just<String, String>().mapConst<String, String, String>("...")
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
fun <X, A, B> Kind<Kind<ForAndThen, X>, A>.mapConst(arg1: B): AndThen<X, B> =
    arrow.core.AndThen.functor<X>().run {
  this@mapConst.mapConst<A, B>(arg1) as arrow.core.AndThen<X, B>
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
fun <X, A, B> A.mapConst(arg1: Kind<Kind<ForAndThen, X>, B>): AndThen<X, A> =
    arrow.core.AndThen.functor<X>().run {
  this@mapConst.mapConst<A, B>(arg1) as arrow.core.AndThen<X, A>
}

/**
 *  Pairs [B] with [A] returning a Kind<F, Tuple2<B, A>>
 *
 *  Kind<F, A> -> Kind<F, Tuple2<B, A>>
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.*
 * import arrow.core.extensions.andthen.functor.*
 * import arrow.core.*
 *
 *
 *  import arrow.core.extensions.andthen.applicative.just
 *
 *  fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   "Hello".just<String, String>().tupleLeft<String, String, String>("World")
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
fun <X, A, B> Kind<Kind<ForAndThen, X>, A>.tupleLeft(arg1: B): AndThen<X, Tuple2<B, A>> =
    arrow.core.AndThen.functor<X>().run {
  this@tupleLeft.tupleLeft<A, B>(arg1) as arrow.core.AndThen<X, arrow.core.Tuple2<B, A>>
}

/**
 *  Pairs [A] with [B] returning a Kind<F, Tuple2<A, B>>
 *
 *  Kind<F, A> -> Kind<F, Tuple2<A, B>>
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.*
 * import arrow.core.extensions.andthen.functor.*
 * import arrow.core.*
 *
 *
 *  import arrow.core.extensions.andthen.applicative.just
 *
 *  fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   "Hello".just<String, String>().tupleRight<String, String, String>("World")
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
fun <X, A, B> Kind<Kind<ForAndThen, X>, A>.tupleRight(arg1: B): AndThen<X, Tuple2<A, B>> =
    arrow.core.AndThen.functor<X>().run {
  this@tupleRight.tupleRight<A, B>(arg1) as arrow.core.AndThen<X, arrow.core.Tuple2<A, B>>
}

/**
 *  Given [A] is a sub type of [B], re-type this value from Kind<F, A> to Kind<F, B>
 *
 *  Kind<F, A> -> Kind<F, B>
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.*
 * import arrow.core.extensions.andthen.functor.*
 * import arrow.core.*
 *
 *
 *  import arrow.core.extensions.andthen.applicative.just
 *  import arrow.Kind
 *
 *  fun main(args: Array<String>) {
 *   val result: Kind<*, CharSequence> =
 *   //sampleStart
 *   "Hello".just<String, String>().map<String, String, String>({ "$it World" }).widen<String,
 * String, String>()
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
fun <X, B, A : B> Kind<Kind<ForAndThen, X>, A>.widen(): AndThen<X, B> =
    arrow.core.AndThen.functor<X>().run {
  this@widen.widen<B, A>() as arrow.core.AndThen<X, B>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <X> Companion.functor(): AndThenFunctor<X> = functor_singleton as
    arrow.core.extensions.AndThenFunctor<X>
