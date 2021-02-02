package arrow.core.extensions.option.functor

import arrow.Kind
import arrow.core.ForOption
import arrow.core.Option
import arrow.core.Option.Companion
import arrow.core.Tuple2
import arrow.core.extensions.OptionFunctor

/**
 * cached extension
 */
@PublishedApi()
internal val functor_singleton: OptionFunctor = object : arrow.core.extensions.OptionFunctor {}

/**
 *  Transform the [F] wrapped value [A] into [B] preserving the [F] structure
 *  Kind<F, A> -> Kind<F, B>
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.extensions.option.applicative.just
 *  import arrow.core.extensions.option.functor.map
 *
 *  fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   "Hello".just().map { "$it World" }
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
  "map<B>(arg1)"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForOption, A>.map(arg1: Function1<A, B>): Option<B> =
    arrow.core.Option.functor().run {
  this@map.map<A, B>(arg1) as arrow.core.Option<B>
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
    "map<B>(arg1)"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForOption, A>.imap(arg1: Function1<A, B>, arg2: Function1<B, A>): Option<B> =
    arrow.core.Option.functor().run {
  this@imap.imap<A, B>(arg1, arg2) as arrow.core.Option<B>
}

/**
 *  Lifts a function `A -> B` to the [F] structure returning a polymorphic function
 *  that can be applied over all [F] values in the shape of Kind<F, A>
 *
 *  `A -> B -> Kind<F, A> -> Kind<F, B>`
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.extensions.option.applicative.just
 *  import arrow.core.extensions.option.functor.lift
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
  "{ option: Option<A> -> option.map<B>(arg0)}",
  "arrow.core.Option"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> lift(arg0: Function1<A, B>): Function1<Kind<ForOption, A>, Kind<ForOption, B>> =
    arrow.core.Option
   .functor()
   .lift<A, B>(arg0) as kotlin.Function1<arrow.Kind<arrow.core.ForOption, A>,
    arrow.Kind<arrow.core.ForOption, B>>

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
  "void()"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForOption, A>.void(): Option<Unit> = arrow.core.Option.functor().run {
  this@void.void<A>() as arrow.core.Option<kotlin.Unit>
}

/**
 *  Applies [f] to an [A] inside [F] and returns the [F] structure with a tuple of the [A] value and the
 *  computed [B] value as result of applying [f]
 *
 *  Kind<F, A> -> Kind<F, Tuple2<A, B>>
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.extensions.option.applicative.just
 *  import arrow.core.extensions.option.functor.fproduct
 *
 *  fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   "Hello".just().fproduct({ "$it World" })
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
  "fproduct(arg1)"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForOption, A>.fproduct(arg1: Function1<A, B>): Option<Tuple2<A, B>> =
    arrow.core.Option.functor().run {
  this@fproduct.fproduct<A, B>(arg1) as arrow.core.Option<arrow.core.Tuple2<A, B>>
}

/**
 *  Replaces [A] inside [F] with [B] resulting in a Kind<F, B>
 *
 *  Kind<F, A> -> Kind<F, B>
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.extensions.option.applicative.just
 *  import arrow.core.extensions.option.functor.mapConst
 *
 *  fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   "Hello World".just().mapConst("...")
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
  "mapConst(arg1)"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForOption, A>.mapConst(arg1: B): Option<B> = arrow.core.Option.functor().run {
  this@mapConst.mapConst<A, B>(arg1) as arrow.core.Option<B>
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
  "arg1.mapConst(this)"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> A.mapConst(arg1: Kind<ForOption, B>): Option<A> = arrow.core.Option.functor().run {
  this@mapConst.mapConst<A, B>(arg1) as arrow.core.Option<A>
}

/**
 *  Pairs [B] with [A] returning a Kind<F, Tuple2<B, A>>
 *
 *  Kind<F, A> -> Kind<F, Tuple2<B, A>>
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.extensions.option.applicative.just
 *  import arrow.core.extensions.option.functor.tupleLeft
 *
 *  fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   "Hello".just().tupleLeft("World")
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
  "tupleLeft(arg1)"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForOption, A>.tupleLeft(arg1: B): Option<Tuple2<B, A>> =
    arrow.core.Option.functor().run {
  this@tupleLeft.tupleLeft<A, B>(arg1) as arrow.core.Option<arrow.core.Tuple2<B, A>>
}

/**
 *  Pairs [A] with [B] returning a Kind<F, Tuple2<A, B>>
 *
 *  Kind<F, A> -> Kind<F, Tuple2<A, B>>
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.extensions.option.applicative.just
 *  import arrow.core.extensions.option.functor.tupleRight
 *
 *  fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   "Hello".just().tupleRight("World")
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
  "tupleRight(arg1)"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForOption, A>.tupleRight(arg1: B): Option<Tuple2<A, B>> =
    arrow.core.Option.functor().run {
  this@tupleRight.tupleRight<A, B>(arg1) as arrow.core.Option<arrow.core.Tuple2<A, B>>
}

/**
 *  Given [A] is a sub type of [B], re-type this value from Kind<F, A> to Kind<F, B>
 *
 *  Kind<F, A> -> Kind<F, B>
 *
 *  ```kotlin:ank:playground
 *  import arrow.Kind
 *  import arrow.core.extensions.option.applicative.just
 *  import arrow.core.extensions.option.functor.widen
 *
 *  fun main(args: Array<String>) {
 *   val result: Kind<*, CharSequence> =
 *   //sampleStart
 *   "Hello".just().map({ "$it World" }).widen()
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
fun <B, A : B> Kind<ForOption, A>.widen(): Option<B> = arrow.core.Option.functor().run {
  this@widen.widen<B, A>() as arrow.core.Option<B>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "Functor typeclass is deprecated. Use concrete methods on Option",
  level = DeprecationLevel.WARNING
)
inline fun Companion.functor(): OptionFunctor = functor_singleton
