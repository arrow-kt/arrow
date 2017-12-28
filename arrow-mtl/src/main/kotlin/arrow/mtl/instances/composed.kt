package arrow.mtl.instances

import arrow.*
import arrow.instances.*

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

inline fun <reified F, reified G> TraverseFilter<F>.compose(GT: TraverseFilter<G> = traverseFilter<G>(), GA: Applicative<G> = applicative<G>()):
        TraverseFilter<Nested<F, G>> = object : ComposedTraverseFilter<F, G> {
    override fun FT(): Traverse<F> = this@compose

    override fun GT(): TraverseFilter<G> = GT

    override fun GA(): Applicative<G> = GA
}