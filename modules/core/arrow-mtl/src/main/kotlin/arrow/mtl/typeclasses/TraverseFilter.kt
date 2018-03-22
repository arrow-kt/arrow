package arrow.mtl.typeclasses

import arrow.Kind
import arrow.core.*
import arrow.typeclasses.Applicative
import arrow.typeclasses.Traverse

interface TraverseFilter<F> : Traverse<F>, FunctorFilter<F> {

    fun <G, A, B> traverseFilter(GA: Applicative<G>, fa: Kind<F, A>, f: (A) -> Kind<G, Option<B>>): Kind<G, Kind<F, B>>

    override fun <A, B> mapFilter(fa: Kind<F, A>, f: (A) -> Option<B>): Kind<F, B> =
            traverseFilter(Id.applicative(), fa, { Id(f(it)) }).value()

    fun <G, A> filterA(fa: Kind<F, A>, f: (A) -> Kind<G, Boolean>, GA: Applicative<G>): Kind<G, Kind<F, A>> =
            traverseFilter(GA, fa, { a -> GA.map(f(a), { b -> if (b) Some(a) else None }) })

    override fun <A> filter(fa: Kind<F, A>, f: (A) -> Boolean): Kind<F, A> =
            filterA(fa, { Id(f(it)) }, Id.applicative()).value()
}
