package arrow

interface FunctionK<F, G> {

    /**
     * Applies this functor transformation from `F` to `G`
     */
    operator fun <A> invoke(fa: HK<F, A>): HK<G, A>

    companion object {
        fun <F> id(): FunctionK<F, F> = object : FunctionK<F, F> {
            override fun <A> invoke(fa: HK<F, A>): HK<F, A> = fa
        }
    }

}

