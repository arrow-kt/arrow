package katz

/**
 * Traverse, also known as Traversable. Traversal over a structure with an effect.
 */
interface Traverse<F> : Functor<F>, Foldable<F>, Typeclass {

    /**
     * Given a function which returns a G effect, thread this effect through the running of this function on all the
     * values in F, returning an F<B> in a G context.
     */
    fun <G, A, B> traverse(fa: HK<F, A>, f: (A) -> HK<G, B>, GA: Applicative<G>): HK<G, HK<F, B>>

    /**
     * Thread all the G effects through the F structure to invert the structure from F<G<A>> to G<F<A>>.
     */
    fun <G, A> sequence(fga: HK<F, HK<G, A>>, GA: Applicative<G>): HK<G, HK<F, A>> =
            traverse(fga, { it }, GA)

    fun <G, A, B> flatTraverse(fa: HK<F, A>, f: (A) -> HK<G, HK<F, B>>, GA: Applicative<G>, FM: Monad<F>): HK<G, HK<F, B>> =
            GA.map(traverse(fa, f, GA), { FM.flatten(it) })

    override fun <A, B> map(fa: HK<F, A>, f: (A) -> B): HK<F, B> =
            traverse(fa, { Id(f(it)) }, Id).value()
}

inline fun <reified F> traverse(): Traverse<F> =
        instance(InstanceParametrizedType(Traverse::class.java, listOf(F::class.java)))