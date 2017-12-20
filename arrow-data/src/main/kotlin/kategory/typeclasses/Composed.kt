package arrow

/**
 * https://www.youtube.com/watch?v=wvSP5qYiz4Y
 */
interface Nested<out F, out G>

typealias NestedType<F, G, A> = HK<Nested<F, G>, A>

typealias UnnestedType<F, G, A> = HK<F, HK<G, A>>

@Suppress("UNCHECKED_CAST")
fun <F, G, A> UnnestedType<F, G, A>.nest(): NestedType<F, G, A> = this as HK<Nested<F, G>, A>

@Suppress("UNCHECKED_CAST")
fun <F, G, A> NestedType<F, G, A>.unnest(): HK<F, HK<G, A>> = this as HK<F, HK<G, A>>

@Suppress("UNCHECKED_CAST")
fun <F, G, A, B> HK2<F, HK2<G, A, B>, HK2<G, A, B>>.binest(): HK2<Nested<F, G>, A, B> = this as HK2<Nested<F, G>, A, B>

@Suppress("UNCHECKED_CAST")
fun <F, G, A, B> HK2<Nested<F, G>, A, B>.biunnest(): HK2<F, HK2<G, A, B>, HK2<G, A, B>> = this as HK2<F, HK2<G, A, B>, HK2<G, A, B>>

interface ComposedFoldable<F, G> :
        Foldable<Nested<F, G>> {

    fun FF(): Foldable<F>

    fun GF(): Foldable<G>

    override fun <A, B> foldLeft(fa: HK<Nested<F, G>, A>, b: B, f: (B, A) -> B): B =
            FF().foldLeft(fa.unnest(), b, { bb, aa -> GF().foldLeft(aa, bb, f) })

    fun <A, B> foldLC(fa: HK<F, HK<G, A>>, b: B, f: (B, A) -> B): B = foldLeft(fa.nest(), b, f)

    override fun <A, B> foldRight(fa: HK<Nested<F, G>, A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
            FF().foldRight(fa.unnest(), lb, { laa, lbb -> GF().foldRight(laa, lbb, f) })

    fun <A, B> foldRC(fa: HK<F, HK<G, A>>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> = foldRight(fa.nest(), lb, f)

    companion object {
        operator fun <F, G> invoke(FF: Foldable<F>, GF: Foldable<G>): ComposedFoldable<F, G> =
                object : ComposedFoldable<F, G> {
                    override fun FF(): Foldable<F> = FF

                    override fun GF(): Foldable<G> = GF
                }
    }
}

inline fun <F, reified G> Foldable<F>.compose(GT: Foldable<G> = foldable<G>()): ComposedFoldable<F, G> = object :
        ComposedFoldable<F, G> {
    override fun FF(): Foldable<F> = this@compose

    override fun GF(): Foldable<G> = GT
}

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

interface ComposedTraverse<F, G> :
        Traverse<Nested<F, G>>,
        ComposedFoldable<F, G> {

    fun FT(): Traverse<F>

    fun GT(): Traverse<G>

    fun GA(): Applicative<G>

    override fun FF(): Foldable<F> = FT()

    override fun GF(): Foldable<G> = GT()

    override fun <H, A, B> traverse(fa: HK<Nested<F, G>, A>, f: (A) -> HK<H, B>, HA: Applicative<H>): HK<H, HK<Nested<F, G>, B>> =
            HA.map(FT().traverse(fa.unnest(), { ga -> GT().traverse(ga, f, HA) }, HA), { it.nest() })

    fun <H, A, B> traverseC(fa: HK<F, HK<G, A>>, f: (A) -> HK<H, B>, HA: Applicative<H>): HK<H, HK<Nested<F, G>, B>> = traverse(fa.nest(), f, HA)

    companion object {
        operator fun <F, G> invoke(
                FF: Traverse<F>,
                GF: Traverse<G>,
                GA: Applicative<G>): ComposedTraverse<F, G> =
                object : ComposedTraverse<F, G> {
                    override fun FT(): Traverse<F> = FF

                    override fun GT(): Traverse<G> = GF

                    override fun GA(): Applicative<G> = GA
                }
    }
}

inline fun <reified F, reified G> Traverse<F>.compose(GT: Traverse<G> = traverse<G>(), GA: Applicative<G> = applicative<G>()): Traverse<Nested<F, G>> =
        object :
                ComposedTraverse<F, G> {
            override fun FT(): Traverse<F> = this@compose

            override fun GT(): Traverse<G> = GT

            override fun GA(): Applicative<G> = GA
        }

interface ComposedSemigroupK<F, G> : SemigroupK<Nested<F, G>> {

    fun F(): SemigroupK<F>

    override fun <A> combineK(x: HK<Nested<F, G>, A>, y: HK<Nested<F, G>, A>): HK<Nested<F, G>, A> = F().combineK(x.unnest(), y.unnest()).nest()

    fun <A> combineKC(x: HK<F, HK<G, A>>, y: HK<F, HK<G, A>>): HK<Nested<F, G>, A> = combineK(x.nest(), y.nest())

    companion object {
        operator fun <F, G> invoke(SF: SemigroupK<F>): SemigroupK<Nested<F, G>> =
                object : ComposedSemigroupK<F, G> {
                    override fun F(): SemigroupK<F> = SF
                }
    }
}

inline fun <F, G> SemigroupK<F>.compose(): SemigroupK<Nested<F, G>> = object : ComposedSemigroupK<F, G> {
    override fun F(): SemigroupK<F> = this@compose
}

interface ComposedMonoidK<F, G> : MonoidK<Nested<F, G>>, ComposedSemigroupK<F, G> {

    override fun F(): MonoidK<F>

    override fun <A> empty(): HK<Nested<F, G>, A> = F().empty<HK<G, A>>().nest()

    fun <A> emptyC(): HK<F, HK<G, A>> = empty<A>().unnest()

    companion object {
        operator fun <F, G> invoke(MK: MonoidK<F>): MonoidK<Nested<F, G>> =
                object : ComposedMonoidK<F, G> {
                    override fun F(): MonoidK<F> = MK
                }
    }
}

fun <F, G> MonoidK<F>.compose(): MonoidK<Nested<F, G>> = object : ComposedMonoidK<F, G> {
    override fun F(): MonoidK<F> = this@compose
}

interface ComposedFunctor<F, G> : Functor<Nested<F, G>> {
    fun F(): Functor<F>

    fun G(): Functor<G>

    override fun <A, B> map(fa: HK<Nested<F, G>, A>, f: (A) -> B): HK<Nested<F, G>, B> = F().map(fa.unnest(), { G().map(it, f) }).nest()

    fun <A, B> mapC(fa: HK<F, HK<G, A>>, f: (A) -> B): HK<F, HK<G, B>> = map(fa.nest(), f).unnest()

    companion object {
        operator fun <F, G> invoke(FF: Functor<F>, GF: Functor<G>): Functor<Nested<F, G>> =
                object : ComposedFunctor<F, G> {
                    override fun F(): Functor<F> = FF

                    override fun G(): Functor<G> = GF
                }
    }
}

inline fun <reified F, reified G> Functor<F>.compose(GF: Functor<G>): Functor<Nested<F, G>> = ComposedFunctor(this, GF)

interface ComposedApplicative<F, G> : Applicative<Nested<F, G>>, ComposedFunctor<F, G> {
    override fun F(): Applicative<F>

    override fun G(): Applicative<G>

    override fun <A, B> map(fa: HK<Nested<F, G>, A>, f: (A) -> B): HK<Nested<F, G>, B> = ap(fa, pure(f))

    override fun <A> pure(a: A): HK<Nested<F, G>, A> = F().pure(G().pure(a)).nest()

    override fun <A, B> ap(fa: HK<Nested<F, G>, A>, ff: HK<Nested<F, G>, (A) -> B>):
            HK<Nested<F, G>, B> = F().ap(fa.unnest(), F().map(ff.unnest(), { gfa: HK<G, (A) -> B> -> { ga: HK<G, A> -> G().ap(ga, gfa) } })).nest()

    fun <A, B> apC(fa: HK<F, HK<G, A>>, ff: HK<F, HK<G, (A) -> B>>): HK<F, HK<G, B>> = ap(fa.nest(), ff.nest()).unnest()

    companion object {
        operator fun <F, G> invoke(FF: Applicative<F>, GF: Applicative<G>)
                : Applicative<Nested<F, G>> =
                object : ComposedApplicative<F, G> {
                    override fun F(): Applicative<F> = FF

                    override fun G(): Applicative<G> = GF
                }
    }
}

inline fun <reified F, reified G> Applicative<F>.compose(GA: Applicative<G> = applicative<G>()): Applicative<Nested<F, G>> = ComposedApplicative(this, GA)

interface ComposedAlternative<F, G> : Alternative<Nested<F, G>>, ComposedApplicative<F, G>, ComposedMonoidK<F, G> {
    override fun F(): Alternative<F>

    companion object {
        operator fun <F, G> invoke(AF: Alternative<F>, AG: Applicative<G>)
                : Alternative<Nested<F, G>> =
                object : ComposedAlternative<F, G> {
                    override fun F(): Alternative<F> = AF

                    override fun G(): Applicative<G> = AG
                }
    }
}

inline fun <reified F, reified G> Alternative<F>.compose(GA: Applicative<G> = applicative<G>()): Alternative<Nested<F, G>> = ComposedAlternative(this, GA)

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

interface ComposedBifoldable<F, G> : Bifoldable<Nested<F, G>> {
    fun F(): Bifoldable<F>

    fun G(): Bifoldable<G>

    override fun <A, B, C> bifoldLeft(fab: HK2<Nested<F, G>, A, B>, c: C, f: (C, A) -> C, g: (C, B) -> C): C =
            F().bifoldLeft(fab.biunnest(), c,
                    { cc: C, gab: HK2<G, A, B> -> G().bifoldLeft(gab, cc, f, g) },
                    { cc: C, gab: HK2<G, A, B> -> G().bifoldLeft(gab, cc, f, g) })

    override fun <A, B, C> bifoldRight(fab: HK2<Nested<F, G>, A, B>, c: Eval<C>, f: (A, Eval<C>) -> Eval<C>, g: (B, Eval<C>) -> Eval<C>): Eval<C> =
            F().bifoldRight(fab.biunnest(), c,
                    { gab: HK2<G, A, B>, cc: Eval<C> -> G().bifoldRight(gab, cc, f, g) },
                    { gab: HK2<G, A, B>, cc: Eval<C> -> G().bifoldRight(gab, cc, f, g) })

    fun <A, B, C> bifoldLeftC(fab: HK2<F, HK2<G, A, B>, HK2<G, A, B>>, c: C, f: (C, A) -> C, g: (C, B) -> C): C =
            bifoldLeft(fab.binest(), c, f, g)

    fun <A, B, C> bifoldRightC(fab: HK2<F, HK2<G, A, B>, HK2<G, A, B>>, c: Eval<C>, f: (A, Eval<C>) -> Eval<C>, g: (B, Eval<C>) -> Eval<C>): Eval<C> =
            bifoldRight(fab.binest(), c, f, g)

    companion object {
        operator fun <F, G> invoke(BF: Bifoldable<F>, BG: Bifoldable<G>): ComposedBifoldable<F, G> =
                object : ComposedBifoldable<F, G> {
                    override fun F(): Bifoldable<F> = BF

                    override fun G(): Bifoldable<G> = BG
                }
    }
}

inline fun <reified F, reified G> Bifoldable<F>.compose(BG: Bifoldable<G> = bifoldable()): Bifoldable<Nested<F, G>> = ComposedBifoldable(this, BG)
