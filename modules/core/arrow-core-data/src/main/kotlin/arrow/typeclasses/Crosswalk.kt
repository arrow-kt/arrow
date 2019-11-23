package arrow.typeclasses

import arrow.Kind
import arrow.core.identity

/**
 * Crosswalk is to Align as Traversable is to Applicative.
 *
 * https://teh.id.au/posts/2017/03/29/these-align-crosswalk/
 */
interface Crosswalk<T> : Functor<T>, Foldable<T> {
  /**
   *  {: data-executable='true'}
   *
   *  ```kotlin:ank
   * import arrow.core.extensions.*
   * import arrow.core.extensions.listk.crosswalk.crosswalk
   * import arrow.core.*
   *
   * Listk.crosswalk().run {
   *    crosswalk(ListK.align(), {it.split(":").k()}, listOf("1:2:3:4:5", "6:7:8:9:10", "11:12").k())
   * }
   * ```
   */
  fun <F, A, B> crosswalk(ALIGN: Align<F>, fa: (A) -> Kind<F, B>, a: Kind<T, A>): Kind<F, Kind<T, B>> =
    sequenceL(ALIGN, a.map(fa))

  /**
   * {: data-executable='true'}
   *
   * ```kotlin:ank
   * import arrow.core.extensions.*
   * import arrow.core.extensions.listk.crosswalk.crosswalk
   * import arrow.core.*
   *
   * ListK.crosswalk().run {
   *    val lists = listOf(listOf(listOf(1, 2, 3, 4, 5).k(),
   *                              listOf(6, 7, 8, 9, 10).k(),
   *                              listOf(11, 12).k())
   *
   *    sequenceL(ListK.align(), lists)
   * }
   * ```
   */
  fun <F, A> sequenceL(ALIGN: Align<F>, tfa: Kind<T, Kind<F, A>>): Kind<F, Kind<T, A>> =
    crosswalk(ALIGN, ::identity, tfa)
}
