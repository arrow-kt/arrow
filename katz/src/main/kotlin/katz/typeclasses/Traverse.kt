package katz

/**
 * Traverse, also known as Traversable. Traversal over a structure with an effect.
 */
interface Traverse<F> : Functor<F>, Foldable<F> {

    /**
     * Given a function which returns a G effect, thread this effect through the running of this function on all the
     * values in F, returning an F<B> in a G context.
     */
    fun <G : Applicative<*>, A, B> traverse(fa: HK<F, A>): (f: (A) -> HK<G, B>) -> HK<G, HK<F, B>>

    /**
     * Thread all the G effects through the F structure to invert the structure from F<G<A>> to G<F<A>>.
     */
    fun <G : Applicative<*>, A> sequence(fga: HK<F, HK<G, A>>): HK<G, HK<F, A>> =
            traverse<G, HK<G, A>, A>(fga)({ it })
}
