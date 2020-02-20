package arrow.typeclasses

import arrow.Kind

/**
 * [Divisible] extends [Divide] by providing an empty value
 *
 * ank_macro_hierarchy(arrow.typeclasses.Divisible)
 */
interface Divisible<F> : Divide<F> {

  /**
   * Provides an empty value for `Kind<F, A>`
   */
  fun <A> conquer(): Kind<F, A>
}
