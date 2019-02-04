package arrow.typeclasses

import arrow.Kind
import arrow.core.Tuple2

/**
 * ank_macro_hierarchy(arrow.typeclasses.Semigroupal)
 */
interface Semigroupal<F> {

    /**
     * Multiplicatively combine F<A> and F<B> into F<Tuple2<A, B>>
     */
    fun <A, B> Kind<F, A>.product(y: Kind<F, B>): Kind<F, Tuple2<A, B>>
}
