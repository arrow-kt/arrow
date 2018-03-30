package arrow.recursion.typeclass

import arrow.*
import arrow.typeclasses.*

interface Corecursive<F, G> {
    fun embedT(compFG: Kind<Nested<F, G>, Kind<F, G>>): Kind<F, G>

    fun <A> ana(a: A, f: Coalgebra<Nested<F, G>, A>, FF: Functor<F>, FG: Functor<G>): Kind<F, G> =
            hylo(a, { embedT(it) }, f, ComposedFunctor(FF, FG))

    fun <M, A> anaM(a: A, f: CoalgebraM<M, Nested<F, G>, A>, AG: Applicative<G>, TF: Traverse<F>, TG: Traverse<G>, MM: Monad<M>): Kind<M, Kind<F, G>> =
            hyloM(a, { MM.just(embedT(it)) }, f, ComposedTraverse(TF, TG, AG), MM)
}
