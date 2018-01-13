package arrow.mtl

import arrow.*
import arrow.core.*
import arrow.typeclasses.Functor

@typeclass
interface FunctorFilter<F> : Functor<F>, TC {

    /**
     * A combined map and filter. Filtering is handled via Option instead of Boolean such that the output type B can be different than the input type A.
     */
    fun <A, B> mapFilter(fa: HK<F, A>, f: (A) -> Option<B>): HK<F, B>

    /**
     * Similar to mapFilter but uses a partial function instead of a function that returns an Option.
     */
    fun <A, B> collect(fa: HK<F, A>, f: PartialFunction<A, B>): HK<F, B> =
            mapFilter(fa, f.lift())

    /**
     * "Flatten" out a structure by collapsing Options.
     */
    fun <A> flattenOption(fa: HK<F, Option<A>>): HK<F, A> = mapFilter(fa, { it })

    /**
     * Apply a filter to a structure such that the output structure contains all A elements in the input structure that satisfy the predicate f but none
     * that don't.
     */
    fun <A> filter(fa: HK<F, A>, f: (A) -> Boolean): HK<F, A> =
            mapFilter(fa, { a -> if (f(a)) Some(a) else None })
}