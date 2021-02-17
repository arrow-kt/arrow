package arrow.core.extensions.tuple2.functor

import arrow.Kind
import arrow.core.ForTuple2
import arrow.core.Tuple2
import arrow.core.Tuple2.Companion
import arrow.core.extensions.Tuple2Functor
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
internal val functor_singleton: Tuple2Functor<Any?> = object : Tuple2Functor<Any?> {}

/**
 *  Transform the [F] wrapped value [A] into [B] preserving the [F] structure
 *  Kind<F, A> -> Kind<F, B>
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.*
 * import arrow.core.extensions.tuple2.functor.*
 * import arrow.core.*
 *
 *
 *  import arrow.core.extensions.tuple2.applicative.just
 * import arrow.core.extensions.monoid
 *
 *  fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   "Hello".just<String, String>(String.monoid()).map<String, String, String>({ "$it World" })
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
    "this.a toT arg1(this.b)",
    "arrow.core.toT"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B> Kind<Kind<ForTuple2, F>, A>.map(arg1: Function1<A, B>): Tuple2<F, B> =
  arrow.core.Tuple2.functor<F>().run {
    this@map.map<A, B>(arg1) as arrow.core.Tuple2<F, B>
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
    "this.a toT arg1(this.b)",
    "arrow.core.toT"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B> Kind<Kind<ForTuple2, F>, A>.imap(arg1: Function1<A, B>, arg2: Function1<B, A>):
  Tuple2<F, B> = arrow.core.Tuple2.functor<F>().run {
    this@imap.imap<A, B>(arg1, arg2) as arrow.core.Tuple2<F, B>
  }

/**
 *  Lifts a function `A -> B` to the [F] structure returning a polymorphic function
 *  that can be applied over all [F] values in the shape of Kind<F, A>
 *
 *  `A -> B -> Kind<F, A> -> Kind<F, B>`
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.*
 * import arrow.core.extensions.tuple2.functor.*
 * import arrow.core.*
 *
 *
 *  import arrow.core.extensions.tuple2.applicative.just
 * import arrow.core.extensions.monoid
 *
 *  fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   lift<String, String, String>({ s: CharSequence -> "$s World" })("Hello".just<String,
 * String>(String.monoid()))
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
    "arrow.core.Tuple2.lift"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B> lift(arg0: Function1<A, B>): Function1<Kind<Kind<ForTuple2, F>, A>,
  Kind<Kind<ForTuple2, F>, B>> = arrow.core.Tuple2
  .functor<F>()
  .lift<A, B>(arg0) as kotlin.Function1<arrow.Kind<arrow.Kind<arrow.core.ForTuple2, F>, A>,
  arrow.Kind<arrow.Kind<arrow.core.ForTuple2, F>, B>>

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
    "this.a toT Unit",
    "arrow.core.toT"
  ),
  DeprecationLevel.WARNING
)
fun <F, A> Kind<Kind<ForTuple2, F>, A>.void(): Tuple2<F, Unit> =
  arrow.core.Tuple2.functor<F>().run {
    this@void.void<A>() as arrow.core.Tuple2<F, kotlin.Unit>
  }

/**
 *  Applies [f] to an [A] inside [F] and returns the [F] structure with a tuple of the [A] value and the
 *  computed [B] value as result of applying [f]
 *
 *  Kind<F, A> -> Kind<F, Tuple2<A, B>>
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.*
 * import arrow.core.extensions.tuple2.functor.*
 * import arrow.core.*
 *
 *
 *  import arrow.core.extensions.tuple2.applicative.just
 * import arrow.core.extensions.monoid
 *
 *  fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   "Hello".just<String, String>(String.monoid()).fproduct<String, String, String>({ "$it World" })
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
    "this.copy(b= this.b toT arg1(this.b))",
    "arrow.core.toT"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B> Kind<Kind<ForTuple2, F>, A>.fproduct(arg1: Function1<A, B>): Tuple2<F, Tuple2<A, B>> =
  arrow.core.Tuple2.functor<F>().run {
    this@fproduct.fproduct<A, B>(arg1) as arrow.core.Tuple2<F, arrow.core.Tuple2<A, B>>
  }

/**
 *  Replaces [A] inside [F] with [B] resulting in a Kind<F, B>
 *
 *  Kind<F, A> -> Kind<F, B>
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.*
 * import arrow.core.extensions.tuple2.functor.*
 * import arrow.core.*
 *
 *
 *  import arrow.core.extensions.tuple2.applicative.just
 * import arrow.core.extensions.monoid
 *
 *  fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   "Hello World".just<String, String>(String.monoid()).mapConst<String, String, String>("...")
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
    "this.a toT arg1",
    "arrow.core.toT"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B> Kind<Kind<ForTuple2, F>, A>.mapConst(arg1: B): Tuple2<F, B> =
  arrow.core.Tuple2.functor<F>().run {
    this@mapConst.mapConst<A, B>(arg1) as arrow.core.Tuple2<F, B>
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
    "arg1.a toT this",
    "arrow.core.toT"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B> A.mapConst(arg1: Kind<Kind<ForTuple2, F>, B>): Tuple2<F, A> =
  arrow.core.Tuple2.functor<F>().run {
    this@mapConst.mapConst<A, B>(arg1) as arrow.core.Tuple2<F, A>
  }

/**
 *  Pairs [B] with [A] returning a Kind<F, Tuple2<B, A>>
 *
 *  Kind<F, A> -> Kind<F, Tuple2<B, A>>
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.*
 * import arrow.core.extensions.tuple2.functor.*
 * import arrow.core.*
 *
 *
 *  import arrow.core.extensions.tuple2.applicative.just
 * import arrow.core.extensions.monoid
 *
 *  fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   "Hello".just<String, String>(String.monoid()).tupleLeft<String, String, String>("World")
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
    "this.copy(b= arg2 toT this.b)",
    "arrow.core.toT"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B> Kind<Kind<ForTuple2, F>, A>.tupleLeft(arg1: B): Tuple2<F, Tuple2<B, A>> =
  arrow.core.Tuple2.functor<F>().run {
    this@tupleLeft.tupleLeft<A, B>(arg1) as arrow.core.Tuple2<F, arrow.core.Tuple2<B, A>>
  }

/**
 *  Pairs [A] with [B] returning a Kind<F, Tuple2<A, B>>
 *
 *  Kind<F, A> -> Kind<F, Tuple2<A, B>>
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.*
 * import arrow.core.extensions.tuple2.functor.*
 * import arrow.core.*
 *
 *
 *  import arrow.core.extensions.tuple2.applicative.just
 * import arrow.core.extensions.monoid
 *
 *  fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   "Hello".just<String, String>(String.monoid()).tupleRight<String, String, String>("World")
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
    "this.copy(b= this.b toT arg1)",
    "arrow.core.toT"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B> Kind<Kind<ForTuple2, F>, A>.tupleRight(arg1: B): Tuple2<F, Tuple2<A, B>> =
  arrow.core.Tuple2.functor<F>().run {
    this@tupleRight.tupleRight<A, B>(arg1) as arrow.core.Tuple2<F, arrow.core.Tuple2<A, B>>
  }

/**
 *  Given [A] is a sub type of [B], re-type this value from Kind<F, A> to Kind<F, B>
 *
 *  Kind<F, A> -> Kind<F, B>
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.*
 * import arrow.core.extensions.tuple2.functor.*
 * import arrow.core.*
 *
 *
 *  import arrow.core.extensions.tuple2.applicative.just
 * import arrow.core.extensions.monoid
 *  import arrow.Kind
 *
 *  fun main(args: Array<String>) {
 *   val result: Kind<*, CharSequence> =
 *   //sampleStart
 *   "Hello".just<String, String>(String.monoid()).map<String, String,
 * String>({ "$it World" }).widen<String, String, String>()
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
  ReplaceWith("this"),
  DeprecationLevel.WARNING
)
fun <F, B, A : B> Kind<Kind<ForTuple2, F>, A>.widen(): Tuple2<F, B> =
  arrow.core.Tuple2.functor<F>().run {
    this@widen.widen<B, A>() as arrow.core.Tuple2<F, B>
  }

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("Foldable typeclasses is deprecated. Use concrete methods on Pair")
inline fun <F> Companion.functor(): Tuple2Functor<F> = functor_singleton as
  arrow.core.extensions.Tuple2Functor<F>
