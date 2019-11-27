package arrow.typeclasses

import arrow.Kind
import arrow.core.identity

interface Crosswalk<T> : Functor<T>, Foldable<T> {
  /**
   *  {: data-executable='true'}
   *
   *  ```kotlin:ank
   * import arrow.core.extensions.*
   * import arrow.core.extensions.listk.crosswalk.crosswalk
   * import arrow.core.extensions.listk.align.align
   * import arrow.core.*
   *
   * ListK.crosswalk().run {
   *    crosswalk(ListK.align(), listOf("1:2:3:4:5", "6:7:8:9:10", "11:12").k()) {
   *      it.split(":").k()
   *    }
   * }
   * ```
   */
  fun <F, A, B> crosswalk(ALIGN: Align<F>, a: Kind<T, A>, fa: (A) -> Kind<F, B>): Kind<F, Kind<T, B>> =
    sequenceL(ALIGN, a.map(fa))

  /**
   * {: data-executable='true'}
   *
   * ```kotlin:ank
   * import arrow.core.extensions.*
   * import arrow.core.extensions.listk.crosswalk.crosswalk
   * import arrow.core.extensions.listk.align.align
   * import arrow.core.*
   *
   * ListK.crosswalk().run {
   *    val lists = listOf(listOf(1, 2, 3, 4, 5).k(),
   *                       listOf(6, 7, 8, 9, 10).k(),
   *                       listOf(11, 12).k())
   *
   *    sequenceL(ListK.align(), lists.k())
   * }
   * ```
   */
  fun <F, A> sequenceL(ALIGN: Align<F>, tfa: Kind<T, Kind<F, A>>): Kind<F, Kind<T, A>> =
    crosswalk(ALIGN, tfa, ::identity)
}
