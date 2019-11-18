package arrow.typeclasses

import arrow.Kind

interface Eq1<F> {
  /**
   * Lift an equality test through the type constructor.
   *
   * {: data-executable='true'}
   *
   * ```kotlin:ank
   * import arrow.core.extensions.option.eq1.eq1
   * import arrow.core.extensions.*
   * import arrow.core.*
   *
   * fun main(args: Array<String>) {
   *    // sampleStart
   *    val stringEq: (String, String) -> Boolean = {a,b -> a == b}
   *    val liftedEq = Option.eq1().liftEq(stringEq)
   *    val result = liftedEq(Some("hello"), Some("kotlin"))
   *    // sampleEnd
   *    println(result)
   * }
   * ```
   */
  fun <A> liftEq(eq: (A, A) -> Boolean): (Kind<F, A>, Kind<F, A>) -> Boolean

  /**
   * Lifts the equality check provided by an given Eq instance
   *
   * {: data-executable='true'}
   *
   * ```kotlin:ank
   * import arrow.core.extensions.option.eq1.eq1
   * import arrow.core.extensions.*
   * import arrow.core.*
   *
   * fun main(args: Array<String>) {
   *    // sampleStart
   *    val liftedEq = Option.eq1().eq1(String.eq())
   *    val result = liftedEq(Some("hello"), Some("kotlin"))
   *    // sampleEnd
   *    println(result)
   * }
   * ```
   */
  fun <A> eq1(EQ: Eq<A>): (Kind<F, A>, Kind<F, A>) -> Boolean =
    liftEq { a: A, b: A ->
      EQ.run {
        a.eqv(b)
      }
    }
}
