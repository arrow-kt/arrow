package arrow

typealias Algebra <F, A> = (HK<F, A>) -> A

typealias AlgebraM <M, F, A> = (HK<F, A>) -> HK<M, A>

typealias Coalgebra <F, A> = (A) -> HK<F, A>

typealias CoalgebraM <M, F, A> = (A) -> HK<M, HK<F, A>>

fun <F, A, B> hylo(a: A, alg: Algebra<F, B>, coalg: Coalgebra<F, A>, FF: Functor<F>): B =
        alg(FF.map(coalg(a), { hylo(it, alg, coalg, FF) }))

fun <M, F, A, B> hyloM(a: A, algM: AlgebraM<M, F, B>, coalgM: CoalgebraM<M, F, A>, TF: Traverse<F>, MM: Monad<M>): HK<M, B> =
        hylo(
                a,
                { MM.flatMap(it.unnest(), { MM.flatMap(TF.sequence(MM, it), algM) }) },
                { aa: A -> coalgM(aa).nest() },
                ComposedFunctor(MM, TF)
        )

fun <F, G> algebraIso(alg: Algebra<Nested<F, G>, HK<F, G>>, coalg: Coalgebra<Nested<F, G>, HK<F, G>>): Birecursive<F, G> =
        object : Birecursive<F, G> {
            override fun embedT(compFG: HK<Nested<F, G>, HK<F, G>>): HK<F, G> =
                    alg(compFG)

            override fun projectT(fg: HK<F, G>): HK<Nested<F, G>, HK<F, G>> =
                    coalg(fg)
        }