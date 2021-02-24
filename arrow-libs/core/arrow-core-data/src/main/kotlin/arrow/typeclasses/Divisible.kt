package arrow.typeclasses

import arrow.Kind
import arrow.KindDeprecation

@Deprecated(KindDeprecation)
/**
 * [Divisible] extends [Divide] by providing an empty value
 */
interface Divisible<F> : Divide<F> {

  /**
   * Provides an empty value for `Kind<F, A>`
   */
  fun <A> conquer(): Kind<F, A>
}
