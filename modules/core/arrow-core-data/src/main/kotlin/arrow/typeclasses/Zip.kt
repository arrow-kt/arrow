package arrow.typeclasses

import arrow.Kind
import arrow.core.Tuple2

/**
 * Zip is a Functor supporting a zip operation that takes the intersection of non-uniform shapes.
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
    zipWith({ a: A, b: B -> Tuple2(a, b) }, other)

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
   *    listOf("A", "B").k().zipWith({"- $it -"}, listOf(1, 2, 3).k())
   *   }
   *   //sampleEnd
   *   println(result)
   */
  fun <A, B, C> Kind<F, A>.zipWith(f: (A, B) -> C, other: Kind<F, B>): Kind<F, C> =
    zip(other).map { f(it.a, it.b) }
}
