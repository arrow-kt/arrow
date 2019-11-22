package arrow.typeclasses

import arrow.Kind
import arrow.core.Ior
import arrow.core.Tuple2

/**
 * Unalign extends Semialign thereby supporting an inverse function to align: It splits an union shape
 * into a tuple representing the component parts.
 */
interface Unalign<F> : Semialign<F> {
  /**
   * splits an union into its component parts.
   *
   * {: data-executable='true'}
   *
   * ```kotlin:ank
   * import arrow.core.extensions.*
   * import arrow.core.extensions.listk.unalign.unalign
   * import arrow.core.*
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val result = ListK.unalign().run {
   *    unalign(listOf(1.leftIor(), 2.rightIor(), (1 toT 2).bothIor()).k())
   *   }
   *   //sampleEnd
   *   println(result)
   * }
   * ```
   */
  fun <A, B> unalign(ior: Kind<F, Ior<A, B>>): Tuple2<Kind<F, A>, Kind<F, B>>

  /**
   * after applying the given function, splits the resulting union shaped structure into its components parts
   *
   * {: data-executable='true'}
   *
   * ```kotlin:ank
   * import arrow.core.extensions.*
   * import arrow.core.extensions.listk.unalign.unalign
   * import arrow.core.*
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val result = ListK.unalign().run {
   *    unalignWith({it.leftIor()}, listOf(1, 2, 3).k())
   *   }
   *   //sampleEnd
   *   println(result)
   * }
   * ```
   */
  fun <A, B, C> unalignWith(fa: (C) -> Ior<A, B>, c: Kind<F, C>): Tuple2<Kind<F, A>, Kind<F, B>> =
    unalign(c.map(fa))
}
