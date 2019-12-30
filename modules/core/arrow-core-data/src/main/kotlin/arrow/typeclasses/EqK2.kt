package arrow.typeclasses

import arrow.Kind2

/**
 * The `EqK2` typeclass abstracts the ability to lift the Eq class to binary type constructors.
 */
interface EqK2<F> {

  /**
   * Lifts the equality check provided by given Eq<A> and Eq<B> instances to Eq<Kind2<F, A, B>>
   *
   * {: data-executable='true'}
   *
   * ```kotlin:ank
   * import arrow.core.extensions.either.eqK2.eqK2
   * import arrow.core.extensions.*
   * import arrow.core.*
   *
   * fun main(args: Array<String>) {
   *    // sampleStart
   *    val result = Either.eqK2().run { Either.right("hello").eqK(Either.right("kotlin"), String.eq(), String.eq()) }
   *    // sampleEnd
   *    println(result)
   * }
   * ```
   */
  fun <A, B> Kind2<F, A, B>.eqK(other: Kind2<F, A, B>, EQA: Eq<A>, EQB: Eq<B>): Boolean

  /**
   * Lifts the equality check provided by given Eq<A> and Eq<B> instances to Eq<Kind2<F, A, B>>
   *
   * {: data-executable='true'}
   *
   * ```kotlin:ank
   * import arrow.core.extensions.either.eqK2.eqK2
   * import arrow.core.extensions.*
   * import arrow.core.*
   *
   * fun main(args: Array<String>) {
   *    // sampleStart
   *    val EQ = Either.eqK2().liftEq(String.eq(), String.eq())
   *    val result = EQ.run { Either.right("hello").eqv(Either.right("kotlin")) }
   *    // sampleEnd
   *    println(result)
   * }
   * ```
   */
  fun <A, B> liftEq(EQA: Eq<A>, EQB: Eq<B>): Eq<Kind2<F, A, B>> =
    Eq { a, b ->
      this@EqK2.run { a.eqK(b, EQA, EQB) }
    }
}
