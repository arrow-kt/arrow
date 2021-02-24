package arrow.core.extensions.either.functor

import arrow.Kind
import arrow.core.Either
import arrow.core.Either.Companion
import arrow.core.ForEither
import arrow.core.Tuple2
import arrow.core.widen as _widen
import arrow.core.extensions.EitherFunctor
import arrow.core.fix
import kotlin.Any
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.Unit
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val functor_singleton: EitherFunctor<Any?> = object : EitherFunctor<Any?> {}

/**
 *  Transform the [F] wrapped value [A] into [B] preserving the [F] structure
 *  Kind<F, A> -> Kind<F, B>
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.*
 * import arrow.core.extensions.either.functor.*
 * import arrow.core.*
 *
 *
 *  import arrow.core.extensions.either.applicative.just
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
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("map(arg1)"))
fun <L, A, B> Kind<Kind<ForEither, L>, A>.map(arg1: Function1<A, B>): Either<L, B> =
  fix().map(arg1)

@JvmName("imap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("map(arg1)"))
fun <L, A, B> Kind<Kind<ForEither, L>, A>.imap(arg1: Function1<A, B>, arg2: Function1<B, A>): Either<L, B> =
  fix().map(arg1)

/**
 *  Lifts a function `A -> B` to the [F] structure returning a polymorphic function
 *  that can be applied over all [F] values in the shape of Kind<F, A>
 *
 *  `A -> B -> Kind<F, A> -> Kind<F, B>`
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.*
 * import arrow.core.extensions.either.functor.*
 * import arrow.core.*
 *
 *
 *  import arrow.core.extensions.either.applicative.just
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
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Either.lift(arg0)", "arrow.core.lift"))
fun <L, A, B> lift(arg0: Function1<A, B>): Function1<Kind<Kind<ForEither, L>, A>, Kind<Kind<ForEither, L>, B>> =
  Either.functor<L>()
    .lift<A, B>(arg0) as kotlin.Function1<arrow.Kind<arrow.Kind<arrow.core.ForEither, L>, A>,
    arrow.Kind<arrow.Kind<arrow.core.ForEither, L>, B>>

@JvmName("void")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("void()", "arrow.core.void"))
fun <L, A> Kind<Kind<ForEither, L>, A>.void(): Either<L, Unit> =
  fix().void()

/**
 *  Applies [f] to an [A] inside [F] and returns the [F] structure with a tuple of the [A] value and the
 *  computed [B] value as result of applying [f]
 *
 *  Kind<F, A> -> Kind<F, Tuple2<A, B>>
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.*
 * import arrow.core.extensions.either.functor.*
 * import arrow.core.*
 *
 *
 *  import arrow.core.extensions.either.applicative.just
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
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("fproduct(arg1)"))
fun <L, A, B> Kind<Kind<ForEither, L>, A>.fproduct(arg1: Function1<A, B>): Either<L, Tuple2<A, B>> =
  fix().fproduct(arg1).map { (a, b) -> Tuple2(a, b) }

/**
 *  Replaces [A] inside [F] with [B] resulting in a Kind<F, B>
 *
 *  Kind<F, A> -> Kind<F, B>
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.*
 * import arrow.core.extensions.either.functor.*
 * import arrow.core.*
 *
 *
 *  import arrow.core.extensions.either.applicative.just
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
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("mapConst(arg1)"))
fun <L, A, B> Kind<Kind<ForEither, L>, A>.mapConst(arg1: B): Either<L, B> =
  fix().mapConst(arg1)

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
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("arg1.mapConst(this)"))
fun <L, A, B> A.mapConst(arg1: Kind<Kind<ForEither, L>, B>): Either<L, A> =
  arg1.fix().mapConst(this)

/**
 *  Pairs [B] with [A] returning a Kind<F, Tuple2<B, A>>
 *
 *  Kind<F, A> -> Kind<F, Tuple2<B, A>>
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.*
 * import arrow.core.extensions.either.functor.*
 * import arrow.core.*
 *
 *
 *  import arrow.core.extensions.either.applicative.just
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
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("tupleLeft(arg1"))
fun <L, A, B> Kind<Kind<ForEither, L>, A>.tupleLeft(arg1: B): Either<L, Tuple2<B, A>> =
  fix().tupleLeft(arg1).map { (a, b) -> Tuple2(a, b) }

/**
 *  Pairs [A] with [B] returning a Kind<F, Tuple2<A, B>>
 *
 *  Kind<F, A> -> Kind<F, Tuple2<A, B>>
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.*
 * import arrow.core.extensions.either.functor.*
 * import arrow.core.*
 *
 *
 *  import arrow.core.extensions.either.applicative.just
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
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("tupleRight(arg1"))
fun <L, A, B> Kind<Kind<ForEither, L>, A>.tupleRight(arg1: B): Either<L, Tuple2<A, B>> =
  fix().tupleRight(arg1).map { (a, b) -> Tuple2(a, b) }

/**
 *  Given [A] is a sub type of [B], re-type this value from Kind<F, A> to Kind<F, B>
 *
 *  Kind<F, A> -> Kind<F, B>
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.*
 * import arrow.core.extensions.either.functor.*
 * import arrow.core.*
 *
 *
 *  import arrow.core.extensions.either.applicative.just
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
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("widen()", "arrow.core.widen"))
fun <L, B, A : B> Kind<Kind<ForEither, L>, A>.widen(): Either<L, B> =
  fix()._widen()

/**
 *  The [Functor] type class abstracts the ability to [map] over the computational context of a type constructor.
 *  Examples of type constructors that can implement instances of the Functor type class include
 *  [arrow.core.Option], [arrow.core.NonEmptyList], [List] and many other data types that include a [map] function with the shape
 *  `fun <F, A, B> Kind<F, A>.map(f: (A) -> B): Kind<F, B>` where `F` refers to any type constructor whose contents can be transformed.
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.*
 * import arrow.core.extensions.either.functor.*
 * import arrow.core.*
 *
 *
 *
 *  fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   Either.functor<String>()
 *   //sampleEnd
 *   println(result)
 *  }
 *  ```
 *
 *  ### Example
 *
 *  Oftentimes we find ourselves in situations where we need to transform the contents of some data type.
 *  [map] allows us to safely compute over values under the assumption that they'll be there returning the
 *  transformation encapsulated in the same context.
 *
 *  Consider [arrow.core.Option] and [arrow.core.Either]:
 *
 *  `Option<A>` allows us to model absence and has two possible states, `Some(a: A)` if the value is not absent and `None` to represent an empty case.
 *  In a similar fashion `Either<L, R>` may have two possible cases `Left(l: L)` and `Right(r: R)`. By convention, `Left` is used to model the exceptional
 *  case and `Right` for the successful case.
 *
 *  Both [arrow.core.Either] and [arrow.core.Option] are examples of data types that can be computed over transforming their inner results.
 *
 *  ```kotlin:ank:playground
 *  import arrow.*
 *  import arrow.core.*
 *
 *  suspend fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   Either.catch { "1".toInt() }.map { it * 2 }
 *   //sampleEnd
 *   println(result)
 *  }
 *  ```
 *
 *  ```kotlin:ank:playground
 *  import arrow.*
 *  import arrow.core.*
 *
 *  fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   Option(1).map { it * 2 }
 *   //sampleEnd
 *   println(result)
 *  }
 *  ```
 */
@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("Functor typeclasses is deprecated. Use concrete methods on Either")
inline fun <L> Companion.functor(): EitherFunctor<L> = functor_singleton as
  arrow.core.extensions.EitherFunctor<L>
