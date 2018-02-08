package arrow.mtl

import arrow.*
import arrow.core.*
import arrow.typeclasses.Applicative
import arrow.typeclasses.Traverse
import arrow.typeclasses.applicative

@typeclass
interface TraverseFilter<F> : Traverse<F>, FunctorFilter<F>, TC {

    fun <G, A, B> traverseFilter(fa: Kind<F, A>, f: (A) -> Kind<G, Option<B>>, GA: Applicative<G>): Kind<G, Kind<F, B>>

    override fun <A, B> mapFilter(fa: Kind<F, A>, f: (A) -> Option<B>): Kind<F, B> =
            traverseFilter(fa, { Id(f(it)) }, Id.applicative()).value()

    fun <G, A> filterA(fa: Kind<F, A>, f: (A) -> Kind<G, Boolean>, GA: Applicative<G>): Kind<G, Kind<F, A>> =
            traverseFilter(fa, { a -> GA.map(f(a), { b -> if (b) Some(a) else None }) }, GA)

    override fun <A> filter(fa: Kind<F, A>, f: (A) -> Boolean): Kind<F, A> =
            filterA(fa, { Id(f(it)) }, Id.applicative()).value()
}

inline fun <reified F, reified G, A, B> Kind<F, A>.traverseFilter(
        FT: TraverseFilter<F> = traverseFilter<F>(),
        GA: Applicative<G> = applicative<G>(),
        noinline f: (A) -> Kind<G, Option<B>>): Kind<G, Kind<F, B>> = FT.traverseFilter(this, f, GA)