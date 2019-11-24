package arrow.typeclasses

import arrow.Kind

/**
 * The `EqK` typeclass abstracts the ability to lift the Eq class to unary type constructors.
 */
interface EqK<F> {

  /**
   * Lifts the equality check provided by an given Eq instance
   *
   * {: data-executable='true'}
   *
   * ```kotlin:ank
   * import arrow.core.extensions.option.eqK.eqK
   * import arrow.core.extensions.*
   * import arrow.core.*
   *
   * fun main(args: Array<String>) {
   *    // sampleStart
   *    val result = Option.eqK().run { Some("hello").eqK(Some("kotlin"), String.eq()) }
   *    // sampleEnd
   *    println(result)
   * }
   * ```
   */
  fun <A> Kind<F, A>.eqK(other: Kind<F, A>, EQ: Eq<A>): Boolean
}
