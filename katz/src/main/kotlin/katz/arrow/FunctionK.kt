package katz

interface FunctionK<in F, out G> {

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

fun <F, G, H> FunctionK<F, G>.or(h: FunctionK<H, G>): FunctionK<CoproductFG<F, H>, G> =
        object : FunctionK<CoproductFG<F, H>, G> {
            override fun <A> invoke(fa: CoproductKind<F, H, A>): HK<G, A> {
                return fa.ev().fold(this@or, h)
            }
        }
