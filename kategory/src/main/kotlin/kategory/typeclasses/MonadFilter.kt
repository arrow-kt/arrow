package kategory

/**
 * a Monad equipped with an additional method which allows us to create an "empty" value for the Monad
 * (for whatever "empty" makes sense for that particular monad). This is of particular interest to us
 * since it allows us to add a filter method to a Monad, which is used when pattern matching or
 * using guards in for comprehensions.
 */
interface MonadFilter<F> : Monad<F>, FunctorFilter<F>, Typeclass {

    fun <A> empty(): HK<F, A>

    override fun <A, B> mapFilter(fa: HK<F, A>, f: (A) -> Option<B>): HK<F, B> =
            flatMap(fa, { a -> f(a).fold({ empty<B>() }, { pure(it) }) })
}

inline fun <reified F> monadFilter(): MonadFilter<F> = instance(InstanceParametrizedType(MonadFilter::class.java, listOf(F::class.java)))
