package arrow.recursion.typeclass

import arrow.Algebra
import arrow.AlgebraM
import arrow.Kind
import arrow.hylo
import arrow.typeclasses.*

interface Recursive<F, G> {
    fun projectT(fg: Kind<F, G>): Kind<Nested<F, G>, Kind<F, G>>

    fun <A> cata(t: Kind<F, G>, f: Algebra<Nested<F, G>, A>, FF: Functor<F>, FG: Functor<G>): A =
            hylo(t, f, this::projectT, ComposedFunctor(FF, FG))

    fun <M, A> cataM(t: Kind<F, G>, f: AlgebraM<M, Nested<F, G>, A>, FF: Functor<F>, FG: Monad<G>, TF: Traverse<F>, TG: Traverse<G>, MM: Monad<M>): Kind<M, A> = MM.run {
        cata(t, { ComposedTraverse(TF, TG, FG).run { it.sequence(MM) }.flatMap(f) }, FF, FG)
    }
}