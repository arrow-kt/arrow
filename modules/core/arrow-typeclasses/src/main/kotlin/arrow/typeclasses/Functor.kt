package arrow.typeclasses

import arrow.Kind
import arrow.core.Tuple2
import arrow.documented

/**
 * ank_macro_hierarchy(arrow.typeclasses.Functor)
 *
 * The [Functor] type class abstracts the ability to `map` over the computational context of a type constructor.
 * Examples of type constructors that can implement instances of the Functor type class include [_dataType_],
 * [Option], [NonEmptyList], [List] and many other data types that include a `map` function with the shape
 * `fun F<A>.map(f: (A) -> B): F<B>` where `F` refers to any type constructor whose contents can be transformed.
 *
 * ```kotlin:ank:playground:extension
 * _imports_
 *
 * fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   _extensionFactory_
 *   //sampleEnd
 *   println(result)
 * }
 * ```
 *
 * The `Functor` typeclass abstracts the ability to `map` over the computational context of a type constructor.
 * Examples of type constructors that can implement instances of the Functor typeclass include `Option`, `NonEmptyList`,
 * `List` and many other datatypes that include a `map` function with the shape `fun F<A>.map(f: (A) -> B): F<B>` where `F`
 * refers to `Option`, `List` or any other type constructor whose contents can be transformed.
 *
 * ### Example
 *
 * Oftentimes we find ourselves in situations where we need to transform the contents of some datatype. `Functor#map` allows
 * us to safely compute over values under the assumption that they'll be there returning the transformation encapsulated in the same context.
 *
 * Consider both `Option` and `Try`:
 *
 * `Option<A>` allows us to model absence and has two possible states, `Some(a: A)` if the value is not absent and `None` to represent an empty case.
 * In a similar fashion `Try<A>` may have two possible cases `Success(a: A)` for computations that succeed and `Failure(e: Throwable)` if they fail
 * with an exception.
 *
 * Both `Try` and `Option` are examples of data types that can be computed over transforming their inner results.
 *
 * ```kotlin:ank:playground
 * import arrow.*
 * import arrow.core.*
 * import arrow.data.*
 *
 * fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   Try { "1".toInt() }.map { it * 2 }
 *   //sampleEnd
 *   println(result)
 * }
 * ```
 *
 * ```kotlin:ank:playground
 * import arrow.*
 * import arrow.core.*
 * import arrow.data.*
 *
 * fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   Option(1).map { it * 2 }
 *   //sampleEnd
 *   println(result)
 * }
 * ```
 *
 */
@documented
interface Functor<F> : Invariant<F> {

  /**
   * Transform the [F] wrapped value [A] into [B] preserving the [F] structure
   * Kind<F, A> -> Kind<F, B>
   *
   * ```kotlin:ank:playground:extension
   * _imports_
   * _imports_applicative_
   *
   * fun main(args: Array<String>) {
   *   val result =
   *   //sampleStart
   *   _just_.map { it + 1 }
   *   //sampleEnd
   *   println(result)
   * }
   * ```
   */
  fun <A, B> Kind<F, A>.map(f: (A) -> B): Kind<F, B>

  override fun <A, B> Kind<F, A>.imap(f: (A) -> B, g: (B) -> A): Kind<F, B> =
    map(f)

  fun <A, B> lift(f: (A) -> B): (Kind<F, A>) -> Kind<F, B> =
    { fa: Kind<F, A> ->
      fa.map(f)
    }

  /**
   * Discards the [A] value inside [F] signaling this container may be pointing to a noop
   * or an effect whose return value is deliberately ignored. The singleton value [Unit] serves as signal.
   *
   * ```kotlin:ank:playground:extension
   * _imports_
   * _imports_applicative_
   *
   * fun main(args: Array<String>) {
   *   val result =
   *   //sampleStart
   *   _just_.void()
   *   //sampleEnd
   *   println(result)
   * }
   * ```
   */
  fun <A> Kind<F, A>.void(): Kind<F, Unit> = map { Unit }

  fun <A, B> Kind<F, A>.fproduct(f: (A) -> B): Kind<F, Tuple2<A, B>> = map { a -> Tuple2(a, f(a)) }

  fun <A, B> Kind<F, A>.`as`(b: B): Kind<F, B> = map { b }

  fun <A, B> Kind<F, A>.tupleLeft(b: B): Kind<F, Tuple2<B, A>> = map { a -> Tuple2(b, a) }

  fun <A, B> Kind<F, A>.tupleRight(b: B): Kind<F, Tuple2<A, B>> = map { a -> Tuple2(a, b) }

  fun <B, A : B> Kind<F, A>.widen(): Kind<F, B> = this
}
