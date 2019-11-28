package arrow.typeclasses

import arrow.Kind

/**
 * The Align type class extends the Semialign type class with a value empty(), which acts as a unit in regards to align.
 */
interface Align<F> : Semialign<F> {
  /**
   * An empty structure.
   *
   * {: data-executable='true'}
   *
   * ```kotlin:ank
   * import arrow.core.extensions.*
   * import arrow.core.extensions.listk.align.align
   * import arrow.core.*
   *
   * ListK.align().run {
   *   align(listOf("A", "B").k(), empty<String>())
   * }
   * ```
   */
  fun <A> empty(): Kind<F, A>
}
