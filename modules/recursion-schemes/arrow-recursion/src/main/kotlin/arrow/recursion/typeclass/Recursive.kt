package arrow.recursion.typeclass

import arrow.*
import arrow.instances.ComposedFunctor
import arrow.instances.ComposedTraverse
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
import arrow.typeclasses.Nested
import arrow.typeclasses.Traverse

interface Recursive<F, G> : TC {
    fun projectT(fg: Kind<F, G>): Kind<Nested<F, G>, Kind<F, G>>

    fun <A> cata(t: Kind<F, G>, f: Algebra<Nested<F, G>, A>, FF: Functor<F>, FG: Functor<G>): A =
            hylo(t, f, this::projectT, ComposedFunctor(FF, FG))

    fun <M, A> cataM(t: Kind<F, G>, f: AlgebraM<M, Nested<F, G>, A>, FF: Functor<F>, FG: Monad<G>, TF: Traverse<F>, TG: Traverse<G>, MM: Monad<M>): Kind<M, A> =
            cata(t, { MM.flatMap(ComposedTraverse(TF, TG, FG).sequence(MM, it), f) }, FF, FG)
}