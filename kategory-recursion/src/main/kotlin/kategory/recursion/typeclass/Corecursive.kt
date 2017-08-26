package kategory

interface Corecursive<F, G> {
    fun FG(): Functor<G>

    fun embedT(compFG: HK<Nested<Nested<F, G>, F>, G>): HK<F, G>

    fun <A> ana(a: A, f: Coalgebra<Nested<F, G>, A>, FF: Functor<F>): HK<F, G> =
            hylo(a, { embedT(it.lift()) }, f, ComposedFunctor(FF, FG()))

    fun <M, A> anaM(a: A, f: CoalgebraM<M, Nested<F, G>, A>, AG: Applicative<G>, TF: Traverse<F>, TG: Traverse<G>, MM: Monad<M>): HK<M, HK<F, G>> =
            hyloM(a, { MM.pure(embedT(it.lift())) }, f, ComposedTraverse(TF, TG, AG), MM)
}