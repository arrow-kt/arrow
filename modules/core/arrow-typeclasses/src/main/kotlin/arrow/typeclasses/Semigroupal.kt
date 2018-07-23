package arrow.typeclasses

import arrow.Kind
import arrow.core.Tuple2

interface Semigroupal<F> {
    /**
     * Combine two contexts.
     */
    fun <A, B> Kind<F, A>.product(fb: Kind<F, B>): Kind<F, Tuple2<A, B>>
}