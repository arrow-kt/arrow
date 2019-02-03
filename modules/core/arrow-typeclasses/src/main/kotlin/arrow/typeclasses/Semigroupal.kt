package arrow.typeclasses

import arrow.Kind
import arrow.core.Either
import arrow.core.Tuple2

/**
 * ank_macro_hierarchy(arrow.typeclasses.Semigroupal)
 */
interface Semigroupal<F> {

    /**
     * Additively combine two F<A> values
     */
    fun <A, B> Kind<F, A>.combineAddition(y: Kind<F, B>): Kind<F, Either<A, B>>

    /**
     * Multiplicatively combine two F<A> values
     */
    fun <A, B> Kind<F, A>.combineMultiplicate(y: Kind<F, B>): Kind<F, Tuple2<A, B>>
}