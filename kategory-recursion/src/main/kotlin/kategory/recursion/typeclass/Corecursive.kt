package kategory

interface Corecursive<F, G> {
    fun FG(): Functor<G>

    fun embedT(compFG: HK<ComposedType<ComposedType<F, G>, F>, G>): HK<F, G>

    fun <A> ana(a: A, f: Coalgebra<ComposedType<F, G>, A>, FF: Functor<F>): HK<F, G> =
            hylo(a, { embedT(it.lift()) }, f, ComposedFunctor(FF, FG()))
}