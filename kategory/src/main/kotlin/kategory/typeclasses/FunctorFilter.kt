package kategory.typeclasses

import kategory.*

interface FunctorFilter<F> : Functor<F>, Typeclass {

    /**
     * A combined map and filter. Filtering is handled via Option instead of Boolean such that the output type B can be different than the input type A.
     */
    fun <A, B> mapFilter(fa: HK<F, A>, f: (A) -> Option<B>): HK<F, B>

    /**
     * Similar to mapFilter but uses a partial function instead of a function that returns an Option.
     */
    fun <A, B> collect(fa: HK<F, A>, f: PartialFunction<A, B>): HK<F, B> =
            mapFilter(fa, f.lift)

}
