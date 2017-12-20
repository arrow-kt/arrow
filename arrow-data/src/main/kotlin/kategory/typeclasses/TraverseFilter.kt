package arrow

import arrow.Some
import arrow.None

interface TraverseFilter<F> : Traverse<F>, FunctorFilter<F>, Typeclass {

    fun <G, A, B> traverseFilter(fa: HK<F, A>, f: (A) -> HK<G, Option<B>>, GA: Applicative<G>): HK<G, HK<F, B>>

    override fun <A, B> mapFilter(fa: HK<F, A>, f: (A) -> Option<B>): HK<F, B> =
            traverseFilter(fa, { Id(f(it)) }, Id.applicative()).value()

    fun <G, A> filterA(fa: HK<F, A>, f: (A) -> HK<G, Boolean>, GA: Applicative<G>): HK<G, HK<F, A>> =
            traverseFilter(fa, { a -> GA.map(f(a), { b -> if (b) Some(a) else None }) }, GA)

    override fun <A> filter(fa: HK<F, A>, f: (A) -> Boolean): HK<F, A> =
            filterA(fa, { Id(f(it)) }, Id.applicative()).value()

}

inline fun <reified F, reified G, A, B> HK<F, A>.traverseFilter(
        FT: TraverseFilter<F> = traverseFilter<F>(),
        GA: Applicative<G> = applicative<G>(),
        noinline f: (A) -> HK<G, Option<B>>): HK<G, HK<F, B>> = FT.traverseFilter(this, f, GA)

inline fun <reified F> traverseFilter(): TraverseFilter<F> = instance(InstanceParametrizedType(TraverseFilter::class.java, listOf(typeLiteral<F>())))
