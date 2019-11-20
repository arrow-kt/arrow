package arrow.typeclasses

import arrow.Kind

/**
 * The Align type class extends the Semialign type class with a value empty(), which acts as a unit in regards to align.
 */
interface Align<F>: Semialign<F> {
  /**
   * An empty structure.
   */
  fun <A> empty(): Kind<F, A>
}
