package arrow.typeclasses

import arrow.Kind

/**
 * The `EqK` typeclass abstracts the ability to lift the Eq class to unary type constructors.
 */
@Deprecated("Kind/type constructors will be deprecated, so this typeclass will no longer be available from 0.13.0")
interface EqK<F> {

  /**
   * Lifts the equality check provided by an given Eq<A> instance to Eq<Kind<F, A>>
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

  /**
   * Lifts the equality check provided by an given Eq<A> instance to Eq<Kind<F, A>>
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
   *    val EQ = Option.eqK().liftEq(String.eq())
   *    val result = EQ.run { Some("hello").eqv(Some("kotlin")) }
   *    // sampleEnd
   *    println(result)
   * }
   * ```
   */
  fun <A> liftEq(EQ: Eq<A>): Eq<Kind<F, A>> =
    Eq { a, b ->
      this@EqK.run { a.eqK(b, EQ) }
    }
}
