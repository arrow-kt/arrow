package arrow.mtl.instances

import arrow.Kind
import arrow.core.Option
import arrow.mtl.typeclasses.FunctorFilter
import arrow.mtl.typeclasses.TraverseFilter
import arrow.typeclasses.*

interface ComposedFunctorFilter<F, G> : FunctorFilter<Nested<F, G>>, ComposedFunctor<F, G> {

    override fun F(): Functor<F>

    override fun G(): FunctorFilter<G>

    override fun <A, B> mapFilter(fa: Kind<Nested<F, G>, A>, f: (A) -> Option<B>): Kind<Nested<F, G>, B> =
            F().map(fa.unnest(), { G().mapFilter(it, f) }).nest()

    fun <A, B> mapFilterC(fga: Kind<F, Kind<G, A>>, f: (A) -> Option<B>): Kind<F, Kind<G, B>> =
            mapFilter(fga.nest(), f).unnest()

    companion object {
        operator fun <F, G> invoke(FF: Functor<F>, FFG: FunctorFilter<G>): ComposedFunctorFilter<F, G> =
                object : ComposedFunctorFilter<F, G> {
                    override fun F(): Functor<F> = FF

                    override fun G(): FunctorFilter<G> = FFG
                }
    }
}

interface ComposedTraverseFilter<F, G> :
        TraverseFilter<Nested<F, G>>,
        ComposedTraverse<F, G> {

    override fun FT(): Traverse<F>

    override fun GT(): TraverseFilter<G>

    override fun GA(): Applicative<G>

    override fun <H, A, B> traverseFilter(GA: Applicative<H>, fa: Kind<Nested<F, G>, A>, f: (A) -> Kind<H, Option<B>>): Kind<H, Kind<Nested<F, G>, B>> =
            GA.map(FT().run { GA.traverse(fa.unnest(), { ga -> GT().traverseFilter(GA, ga, f) }) }, { it.nest() })

    fun <H, A, B> traverseFilterC(fa: Kind<F, Kind<G, A>>, f: (A) -> Kind<H, Option<B>>, HA: Applicative<H>): Kind<H, Kind<Nested<F, G>, B>> =
            traverseFilter(HA, fa.nest(), f)

    companion object {
        operator fun <F, G> invoke(
                FF: Traverse<F>,
                GF: TraverseFilter<G>,
                GA: Applicative<G>): ComposedTraverseFilter<F, G> =
                object : ComposedTraverseFilter<F, G> {
                    override fun FT(): Traverse<F> = FF

                    override fun GT(): TraverseFilter<G> = GF

                    override fun GA(): Applicative<G> = GA
                }
    }
}
