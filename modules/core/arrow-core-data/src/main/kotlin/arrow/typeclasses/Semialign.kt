package arrow.typeclasses

import arrow.Kind
import arrow.core.Ior
import arrow.core.identity

/**
 * A type class used for aligning of functors with non-uniform shapes.
 *
 * Note: Instances need to override either one of align/unlign here, otherwise a Stackoverflow exception will occur at runtime!
 */
interface Semialign<F> : Functor<F> {
  /**
   * Combines two structures by taking the union of their shapes and using Ior to hold the elements.
   *
   *  {: data-executable='true'}
   *
   * ```kotlin:ank
   * import arrow.core.extensions.*
   * import arrow.core.extensions.listk.semialign.semialign
   * import arrow.core.*
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val result = ListK.semialign().run {
   *    align(listOf("A", "B").k(), listOf(1, 2, 3).k())
   *   }
   *   //sampleEnd
   *   println(result)
   * }
   * ```
   */
  fun <A, B> align(a: Kind<F, A>, b: Kind<F, B>): Kind<F, Ior<A, B>> = alignWith(::identity, a, b)

  /**
   * Combines two structures by taking the union of their shapes and combining the elements with the given function.
   *
   * {: data-executable='true'}
   *
   * ```kotlin:ank
   * import arrow.core.extensions.*
   * import arrow.core.extensions.listk.semialign.semialign
   * import arrow.core.*
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val result = ListK.semialign().run {
   *    alignWith({"$it"}, listOf("A", "B").k(), listOf(1, 2, 3).k())
   *   }
   *   //sampleEnd
   *   println(result)
   * }
   * ```
   */
  fun <A, B, C> alignWith(fa: (Ior<A, B>) -> C, a: Kind<F, A>, b: Kind<F, B>): Kind<F, C> = align(a, b).map(fa)
}
