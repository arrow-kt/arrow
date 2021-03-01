package arrow.typeclasses

import arrow.Kind
import arrow.KindDeprecation

@Deprecated(KindDeprecation)
/**
 * Repeat extends Zip by providing a repeat structure.
 */
interface Repeat<F> : Zip<F> {

  fun <A> repeat(a: A): Kind<F, A>
}
