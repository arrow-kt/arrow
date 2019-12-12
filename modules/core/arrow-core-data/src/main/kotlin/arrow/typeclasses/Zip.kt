package arrow.typeclasses

import arrow.Kind
import arrow.core.Tuple2

/**
 * Zip is a typeclass that extends a Functor by providing a zip operation that takes the intersection of non-uniform shapes.
 */
interface Zip<F> : Semialign<F> {
  /**
   * Combines to structures by taking the intersection of their shapes
   * and using `Tuple2` to hold the elements.
   *
   *  {: data-executable='true'}
   *
   * ```kotlin:ank
   * import arrow.core.extensions.*
   * import arrow.core.extensions.listk.zip.zip
   * import arrow.core.*
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val result = ListK.zip().run {
   *    listOf("A", "B").k().zip(listOf(1, 2, 3).k())
   *   }
   *   //sampleEnd
   *   println(result)
   * }
   * ```
   */
  fun <A, B> Kind<F, A>.zip(other: Kind<F, B>): Kind<F, Tuple2<A, B>> =
    zipWith(other) { a: A, b: B -> Tuple2(a, b) }

  /**
   * Combines to structures by taking the intersection of their shapes
   * and combining the elements with the given function.
   *
   * {: data-executable='true'}
   *
   * ```kotlin:ank
   * import arrow.core.extensions.*
   * import arrow.core.extensions.listk.zip.zip
   * import arrow.core.*
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val result = ListK.zip().run {
   *    listOf("A", "B").k().zipWith(listOf(1, 2, 3).k()) {
   *      a, b -> "$a # $b"
   *    }
   *   }
   *   //sampleEnd
   *   println(result)
   * }
   * ```
   */
  fun <A, B, C> Kind<F, A>.zipWith(other: Kind<F, B>, f: (A, B) -> C): Kind<F, C> =
    zip(other).map { f(it.a, it.b) }
}
