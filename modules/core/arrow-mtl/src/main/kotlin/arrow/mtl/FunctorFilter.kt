package arrow.mtl

import arrow.*
import arrow.core.*
import arrow.typeclasses.Functor

@typeclass
interface FunctorFilter<F> : Functor<F>, TC {

    /**
     * A combined map and filter. Filtering is handled via Option instead of Boolean such that the output type B can be different than the input type A.
     */
    fun <A, B> mapFilter(fa: Kind<F, A>, f: (A) -> Option<B>): Kind<F, B>

    /**
     * Similar to mapFilter but uses a partial function instead of a function that returns an Option.
     */
    fun <A, B> collect(fa: Kind<F, A>, f: PartialFunction<A, B>): Kind<F, B> =
            mapFilter(fa, f.lift())

    /**
     * "Flatten" out a structure by collapsing Options.
     */
    fun <A> flattenOption(fa: Kind<F, Option<A>>): Kind<F, A> = mapFilter(fa, { it })

    /**
     * Apply a filter to a structure such that the output structure contains all A elements in the input structure that satisfy the predicate f but none
     * that don't.
     */
    fun <A> filter(fa: Kind<F, A>, f: (A) -> Boolean): Kind<F, A> =
            mapFilter(fa, { a -> if (f(a)) Some(a) else None })
}