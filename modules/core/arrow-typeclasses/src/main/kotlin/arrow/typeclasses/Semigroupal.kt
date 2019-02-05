package arrow.typeclasses

import arrow.Kind
import arrow.core.Tuple2

/**
 * ank_macro_hierarchy(arrow.typeclasses.Semigroupal)
 */
interface Semigroupal<F> {

    /**
     * Given a type [A], create an "identity" for a F<A> value.
     */
    fun <A> id(): Kind<F, A>

    /**
     * Multiplicatively combine F<A> and F<B> into F<Tuple2<A, B>>
     */
    fun <A, B> Kind<F, A>.product(fb: Kind<F, B>): Kind<F, Tuple2<A, B>>
}
