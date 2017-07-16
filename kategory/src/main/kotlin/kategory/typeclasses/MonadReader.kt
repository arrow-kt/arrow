package kategory

interface MonadReader<F, D> : Monad<F> {
    /** Get the environment */
    fun ask(): HK<F, D>

    /** Modify the environment */
    fun <A> local(f: (D) -> D, fa: HK<F, A>): HK<F, A>

    /** Retrieves a function of the environment */
    fun <A> reader(f: (D) -> A): HK<F, A> = map(ask(), f)
}

inline fun <reified F, reified D> monadReader(): MonadReader<F, D> =
        instance(InstanceParametrizedType(MonadReader::class.java, listOf(F::class.java, D::class.java)))