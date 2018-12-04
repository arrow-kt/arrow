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
 * {: data-executable='true'}
 *
 * ```kotlin:ank
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
 */
@documented
interface Functor<F> : Invariant<F> {

  /**
   * documented map
   * second line doc
   *
   * {: data-executable='true'}
   *
   * ```kotlin:ank
   * import arrow.core.*
   *
   * fun main(args: Array<String>) {
   *   val result =
   *   //sampleStart
   *   Option(1)
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

  fun <A> Kind<F, A>.void(): Kind<F, Unit> = map { Unit }

  fun <A, B> Kind<F, A>.fproduct(f: (A) -> B): Kind<F, Tuple2<A, B>> = map { a -> Tuple2(a, f(a)) }

  fun <A, B> Kind<F, A>.`as`(b: B): Kind<F, B> = map { b }

  fun <A, B> Kind<F, A>.tupleLeft(b: B): Kind<F, Tuple2<B, A>> = map { a -> Tuple2(b, a) }

  fun <A, B> Kind<F, A>.tupleRight(b: B): Kind<F, Tuple2<A, B>> = map { a -> Tuple2(a, b) }

  fun <B, A : B> Kind<F, A>.widen(): Kind<F, B> = this
}
