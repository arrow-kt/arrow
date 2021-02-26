package arrow.typeclasses

import arrow.Kind
import arrow.KindDeprecation

/**
 * The Align type class extends the Semialign type class with a value empty(), which acts as a unit in regards to align.
 */
@Deprecated(KindDeprecation)
interface Align<F> : Semialign<F> {
  fun <A> empty(): Kind<F, A>
}
