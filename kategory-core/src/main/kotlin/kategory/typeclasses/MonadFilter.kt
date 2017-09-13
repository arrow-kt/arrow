package kategory

interface MonadFilter<F> : Monad<F>, FunctorFilter<F>, Typeclass {

    fun <A> empty(): HK<F, A>

    override fun <A, B> mapFilter(fa: HK<F, A>, f: (A) -> Option<B>): HK<F, B> =
            flatMap(fa, { a -> f(a).fold({ empty<B>() }, { pure(it) }) })
}

inline fun <reified F> monadFilter(): MonadFilter<F> = instance(InstanceParametrizedType(MonadFilter::class.java, listOf(typeLiteral<F>())))
