package arrow.typeclasses

import arrow.Kind

/**
 *  MonadPlus is a typeclass that extends a Monad by supporting choice and failure also.
 */
interface MonadPlus<F> : Monad<F>, Alternative<F> {
  /**
   * the identity of `mPlus`
   *
   *  {: data-executable='true'}
   *
   * ```kotlin:ank
   * import arrow.core.extensions.*
   * import arrow.core.extensions.listk.monadPlus.monadPlus
   * import arrow.core.*
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val result = ListK.monadPlus().run {
   *    listOf("A", "B").k().plusM(zeroM())
   *   }
   *   //sampleEnd
   *   println(result)
   * }
   * ```
   */
  fun <A> zeroM(): Kind<F, A> = empty()

  /**
   * an associative operation to combine two structures.
   *
   *  {: data-executable='true'}
   *
   * ```kotlin:ank
   * import arrow.core.extensions.*
   * import arrow.core.extensions.listk.monadPlus.monadPlus
   * import arrow.core.*
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val result = ListK.monadPlus().run {
   *    listOf(1, 2).k().plusM(listOf(3, 4, 5).k())
   *   }
   *   //sampleEnd
   *   println(result)
   * }
   * ```
   */
  fun <A> Kind<F, A>.plusM(other: Kind<F, A>): Kind<F, A> = alt(other)
}
