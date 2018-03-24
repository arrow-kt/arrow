package arrow

import arrow.recursion.typeclass.Birecursive
import arrow.typeclasses.*

typealias Algebra <F, A> = (Kind<F, A>) -> A

typealias AlgebraM <M, F, A> = (Kind<F, A>) -> Kind<M, A>

typealias Coalgebra <F, A> = (A) -> Kind<F, A>

typealias CoalgebraM <M, F, A> = (A) -> Kind<M, Kind<F, A>>

fun <F, A, B> hylo(a: A, alg: Algebra<F, B>, coalg: Coalgebra<F, A>, FF: Functor<F>): B =
        alg(FF.map(coalg(a), { hylo(it, alg, coalg, FF) }))

fun <M, F, A, B> hyloM(a: A, algM: AlgebraM<M, F, B>, coalgM: CoalgebraM<M, F, A>, TF: Traverse<F>, MM: Monad<M>): Kind<M, B> = MM.run {
    hylo(
            a,
            { it.unnest().flatMap({ TF.run { sequence(it) }.flatMap(algM) }) },
            { aa: A -> coalgM(aa).nest() },
            ComposedFunctor(this, TF)
    )
}

fun <F, G> algebraIso(alg: Algebra<Nested<F, G>, Kind<F, G>>, coalg: Coalgebra<Nested<F, G>, Kind<F, G>>): Birecursive<F, G> =
        object : Birecursive<F, G> {
            override fun embedT(compFG: Kind<Nested<F, G>, Kind<F, G>>): Kind<F, G> =
                    alg(compFG)

            override fun projectT(fg: Kind<F, G>): Kind<Nested<F, G>, Kind<F, G>> =
                    coalg(fg)
        }