package arrow

interface Corecursive<F, G> : Typeclass {
    fun embedT(compFG: HK<Nested<F, G>, HK<F, G>>): HK<F, G>

    fun <A> ana(a: A, f: Coalgebra<Nested<F, G>, A>, FF: Functor<F>, FG: Functor<G>): HK<F, G> =
            hylo(a, { embedT(it) }, f, ComposedFunctor(FF, FG))

    fun <M, A> anaM(a: A, f: CoalgebraM<M, Nested<F, G>, A>, AG: Applicative<G>, TF: Traverse<F>, TG: Traverse<G>, MM: Monad<M>): HK<M, HK<F, G>> =
            hyloM(a, { MM.pure(embedT(it)) }, f, ComposedTraverse(TF, TG, AG), MM)
}
