package arrow.mtl.instances

import arrow.*
import arrow.core.Option
import arrow.instances.ComposedFunctor
import arrow.instances.ComposedTraverse
import arrow.mtl.FunctorFilter
import arrow.mtl.TraverseFilter
import arrow.mtl.functorFilter
import arrow.typeclasses.*

interface ComposedFunctorFilter<F, G> : FunctorFilter<Nested<F, G>>, ComposedFunctor<F, G> {

    override fun F(): Functor<F>

    override fun G(): FunctorFilter<G>

    override fun <A, B> mapFilter(fga: HK<Nested<F, G>, A>, f: (A) -> Option<B>): HK<Nested<F, G>, B> =
            F().map(fga.unnest(), { G().mapFilter(it, f) }).nest()

    fun <A, B> mapFilterC(fga: HK<F, HK<G, A>>, f: (A) -> Option<B>): HK<F, HK<G, B>> =
            mapFilter(fga.nest(), f).unnest()

    companion object {
        operator fun <F, G> invoke(FF: Functor<F>, FFG: FunctorFilter<G>): ComposedFunctorFilter<F, G> =
                object : ComposedFunctorFilter<F, G> {
                    override fun F(): Functor<F> = FF

                    override fun G(): FunctorFilter<G> = FFG
                }
    }
}

inline fun <reified F, reified G> Functor<F>.composeFilter(FFG: FunctorFilter<G> = functorFilter()):
        FunctorFilter<Nested<F, G>> = ComposedFunctorFilter(this, FFG)

interface ComposedTraverseFilter<F, G> :
        TraverseFilter<Nested<F, G>>,
        ComposedTraverse<F, G> {

    override fun FT(): Traverse<F>

    override fun GT(): TraverseFilter<G>

    override fun GA(): Applicative<G>

    override fun <H, A, B> traverseFilter(fa: HK<Nested<F, G>, A>, f: (A) -> HK<H, Option<B>>, HA: Applicative<H>): HK<H, HK<Nested<F, G>, B>> =
            HA.map(FT().traverse(fa.unnest(), { ga -> GT().traverseFilter(ga, f, HA) }, HA), { it.nest() })

    fun <H, A, B> traverseFilterC(fa: HK<F, HK<G, A>>, f: (A) -> HK<H, Option<B>>, HA: Applicative<H>): HK<H, HK<Nested<F, G>, B>> =
            traverseFilter(fa.nest(), f, HA)

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

inline fun <reified F, reified G> TraverseFilter<F>.compose(GT: TraverseFilter<G> = arrow.mtl.traverseFilter<G>(), GA: Applicative<G> = applicative<G>()):
        TraverseFilter<Nested<F, G>> = object : ComposedTraverseFilter<F, G> {
    override fun FT(): Traverse<F> = this@compose

    override fun GT(): TraverseFilter<G> = GT

    override fun GA(): Applicative<G> = GA
}