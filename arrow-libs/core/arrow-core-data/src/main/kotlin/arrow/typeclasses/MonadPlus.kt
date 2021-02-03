package arrow.typeclasses

import arrow.Kind
import arrow.KindDeprecation

@Deprecated(KindDeprecation)
/**
 *  MonadPlus is a typeclass that extends a Monad by supporting choice and failure.
 *  It is equal to [Alternative] in its api, but provides additional laws for how `flatMap` and `empty` interact.
 */
interface MonadPlus<F> : Monad<F>, Alternative<F> {
  /**
   * Identity for `mPlus`. [MonadPlus] variant of [empty]
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
   * Associative operation to combine two structures. [MonadPlus] variant of [orElse].
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
