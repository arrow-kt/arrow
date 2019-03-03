package arrow.typeclasses

import arrow.Kind

/**
 * ank_macro_hierarchy(arrow.typeclasses.Monoidal)
 */
interface Monoidal<F> : Semigroupal<F> {

  /**
   * Given a type [A], create an "identity" for a F<A> value.
   */
  fun <A> identity(): Kind<F, A>

  companion object
}
