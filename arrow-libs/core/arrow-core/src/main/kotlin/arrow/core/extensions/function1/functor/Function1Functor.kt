package arrow.core.extensions.function1.functor

import arrow.Kind
import arrow.core.ForFunction1
import arrow.core.Function1.Companion
import arrow.core.Tuple2
import arrow.core.extensions.Function1Functor
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
internal val functor_singleton: Function1Functor<Any?> = object : Function1Functor<Any?> {}

/**
 *  Transform the [F] wrapped value [A] into [B] preserving the [F] structure
 *  Kind<F, A> -> Kind<F, B>
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.*
 * import arrow.core.extensions.function1.functor.*
 * import arrow.core.*
 *
 *
 *  import arrow.core.extensions.function1.applicative.just
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
fun <I, A, B> Kind<Kind<ForFunction1, I>, A>.map(arg1: Function1<A, B>): arrow.core.Function1<I, B> =
  arrow.core.Function1.functor<I>().run {
    this@map.map<A, B>(arg1) as arrow.core.Function1<I, B>
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
fun <I, A, B> Kind<Kind<ForFunction1, I>, A>.imap(arg1: Function1<A, B>, arg2: Function1<B, A>):
    arrow.core.Function1<I, B> = arrow.core.Function1.functor<I>().run {
  this@imap.imap<A, B>(arg1, arg2) as arrow.core.Function1<I, B>
}

/**
 *  Lifts a function `A -> B` to the [F] structure returning a polymorphic function
 *  that can be applied over all [F] values in the shape of Kind<F, A>
 *
 *  `A -> B -> Kind<F, A> -> Kind<F, B>`
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.*
 * import arrow.core.extensions.function1.functor.*
 * import arrow.core.*
 *
 *
 *  import arrow.core.extensions.function1.applicative.just
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
  "arrow.core.Function1.lift"
  ),
  DeprecationLevel.WARNING
)
fun <I, A, B> lift(arg0: Function1<A, B>): Function1<Kind<Kind<ForFunction1, I>, A>,
    Kind<Kind<ForFunction1, I>, B>> = arrow.core.Function1
   .functor<I>()
   .lift<A, B>(arg0) as kotlin.Function1<arrow.Kind<arrow.Kind<arrow.core.ForFunction1, I>, A>,
    arrow.Kind<arrow.Kind<arrow.core.ForFunction1, I>, B>>

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
fun <I, A> Kind<Kind<ForFunction1, I>, A>.void(): arrow.core.Function1<I, Unit> =
    arrow.core.Function1.functor<I>().run {
  this@void.void<A>() as arrow.core.Function1<I, kotlin.Unit>
}

/**
 *  Applies [f] to an [A] inside [F] and returns the [F] structure with a tuple of the [A] value and the
 *  computed [B] value as result of applying [f]
 *
 *  Kind<F, A> -> Kind<F, Tuple2<A, B>>
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.*
 * import arrow.core.extensions.function1.functor.*
 * import arrow.core.*
 *
 *
 *  import arrow.core.extensions.function1.applicative.just
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
fun <I, A, B> Kind<Kind<ForFunction1, I>, A>.fproduct(arg1: Function1<A, B>):
    arrow.core.Function1<I, Tuple2<A, B>> = arrow.core.Function1.functor<I>().run {
  this@fproduct.fproduct<A, B>(arg1) as arrow.core.Function1<I, arrow.core.Tuple2<A, B>>
}

/**
 *  Replaces [A] inside [F] with [B] resulting in a Kind<F, B>
 *
 *  Kind<F, A> -> Kind<F, B>
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.*
 * import arrow.core.extensions.function1.functor.*
 * import arrow.core.*
 *
 *
 *  import arrow.core.extensions.function1.applicative.just
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
fun <I, A, B> Kind<Kind<ForFunction1, I>, A>.mapConst(arg1: B): arrow.core.Function1<I, B> =
    arrow.core.Function1.functor<I>().run {
  this@mapConst.mapConst<A, B>(arg1) as arrow.core.Function1<I, B>
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
fun <I, A, B> A.mapConst(arg1: Kind<Kind<ForFunction1, I>, B>): arrow.core.Function1<I, A> =
    arrow.core.Function1.functor<I>().run {
  this@mapConst.mapConst<A, B>(arg1) as arrow.core.Function1<I, A>
}

/**
 *  Pairs [B] with [A] returning a Kind<F, Tuple2<B, A>>
 *
 *  Kind<F, A> -> Kind<F, Tuple2<B, A>>
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.*
 * import arrow.core.extensions.function1.functor.*
 * import arrow.core.*
 *
 *
 *  import arrow.core.extensions.function1.applicative.just
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
fun <I, A, B> Kind<Kind<ForFunction1, I>, A>.tupleLeft(arg1: B): arrow.core.Function1<I, Tuple2<B,
    A>> = arrow.core.Function1.functor<I>().run {
  this@tupleLeft.tupleLeft<A, B>(arg1) as arrow.core.Function1<I, arrow.core.Tuple2<B, A>>
}

/**
 *  Pairs [A] with [B] returning a Kind<F, Tuple2<A, B>>
 *
 *  Kind<F, A> -> Kind<F, Tuple2<A, B>>
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.*
 * import arrow.core.extensions.function1.functor.*
 * import arrow.core.*
 *
 *
 *  import arrow.core.extensions.function1.applicative.just
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
fun <I, A, B> Kind<Kind<ForFunction1, I>, A>.tupleRight(arg1: B): arrow.core.Function1<I, Tuple2<A,
    B>> = arrow.core.Function1.functor<I>().run {
  this@tupleRight.tupleRight<A, B>(arg1) as arrow.core.Function1<I, arrow.core.Tuple2<A, B>>
}

/**
 *  Given [A] is a sub type of [B], re-type this value from Kind<F, A> to Kind<F, B>
 *
 *  Kind<F, A> -> Kind<F, B>
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.*
 * import arrow.core.extensions.function1.functor.*
 * import arrow.core.*
 *
 *
 *  import arrow.core.extensions.function1.applicative.just
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
fun <I, B, A : B> Kind<Kind<ForFunction1, I>, A>.widen(): arrow.core.Function1<I, B> =
    arrow.core.Function1.functor<I>().run {
  this@widen.widen<B, A>() as arrow.core.Function1<I, B>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <I> Companion.functor(): Function1Functor<I> = functor_singleton as
    arrow.core.extensions.Function1Functor<I>
