package arrow.typeclasses

import arrow.Kind
import arrow.Kind2
import arrow.core.identity

/**
 * ank_macro_hierarchy(arrow.typeclasses.Bifunctor)
 *
 * [Bifunctor] has the same properties of [Functor], but acting onto two types, instead of one.
 *
 * It can be used to map both of its inside types.
 */
interface Bifunctor<F> {
  /**
   * Maps both types of Kind2.
   *
   * {: data-executable='true'}
   *
   * ```kotlin:ank
   * import arrow.core.Tuple2
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val tuple = Tuple2(2, 2).bimap({ num -> num * 2 }, { num -> num + 5})
   *   //sampleEnd
   *   println("First value: ${tuple.a}")
   *   println("Second value: ${tuple.b}")
   * }
   * ```
   */
  fun <A, B, C, D> Kind2<F, A, B>.bimap(fl: (A) -> C, fr: (B) -> D): Kind2<F, C, D>

  /**
   * Lifts two functions to the Bifunctor type.
   *
   * {: data-executable='true'}
   *
   * ```kotlin:ank
   * import arrow.core.*
   * import arrow.core.extensions.tuple2.bifunctor.lift
   *
   * val sumFive : (Int) -> Int = { it + 5 }
   * val sumTwo : (Int) -> Int = { it + 2 }
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val tuple : Tuple2<Int, Int> = lift(sumFive, sumTwo)(Tuple2(2, 2)).fix()
   *   //sampleEnd
   *   println("First value: ${tuple.a}")
   *   println("Second value: ${tuple.b}")
   * }
   *
   * ```
   */
  fun <A, B, C, D> lift(fl: (A) -> C, fr: (B) -> D): (Kind2<F, A, B>) -> Kind2<F, C, D> = { kind2 ->
      kind2.bimap(fl, fr)
    }

  /**
   * Map the left side type of Kind2
   *
   * {: data-executable='true'}
   *
   * ```kotlin:ank
   * import arrow.core.Tuple2
   * import arrow.core.extensions.tuple2.bifunctor.mapLeft
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val tuple = Tuple2(2, 2).mapLeft { num -> num * 2 }
   *   //sampleEnd
   *   println("First value: ${tuple.a}")
   *   println("Second value: ${tuple.b}")
   * }
   * ```
   *
   */
  fun <A, B, C> Kind2<F, A, B>.mapLeft(f: (A) -> C): Kind2<F, C, B> =
    bimap(f, ::identity)

  /**
   * Returns a [Functor] acting on the type of the right side.
   */
  fun <X> rightFunctor(): Functor<Kind<F, X>> = object : RightFunctor<F, X> {
    override val F: Bifunctor<F> = this@Bifunctor
  }

  /**
   * Returns a [Functor] acting on the type of the left side.
   */
  fun <X> leftFunctor(): Functor<Conested<F, X>> = CocomposedFunctor(this@Bifunctor)

  fun <AA, B, A : AA> Kind2<F, A, B>.leftWiden(): Kind2<F, AA, B> = this
}

private interface RightFunctor<F, X> : Functor<Kind<F, X>> {
  val F: Bifunctor<F>

  override fun <A, B> Kind2<F, X, A>.map(f: (A) -> B): Kind2<F, X, B> =
    F.run { bimap(::identity, f) }
}
