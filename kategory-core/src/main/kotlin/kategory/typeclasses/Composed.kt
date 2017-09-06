package kategory

import kategory.typeclasses.Alternative
import kategory.typeclasses.Bifoldable
import kategory.typeclasses.bifoldable

/**
 * https://www.youtube.com/watch?v=wvSP5qYiz4Y
 */
interface ComposedType<out F, out G>

@Suppress("UNCHECKED_CAST")
fun <F, G, A> HK<F, HK<G, A>>.lift(): HK<ComposedType<F, G>, A> = this as HK<ComposedType<F, G>, A>

@Suppress("UNCHECKED_CAST")
fun <F, G, A, B> HK2<F, HK2<G, A, B>, HK2<G, A, B>>.liftB(): HK2<ComposedType<F, G>, A, B> = this as HK2<ComposedType<F, G>, A, B>

@Suppress("UNCHECKED_CAST")
fun <F, G, A> HK<ComposedType<F, G>, A>.lower(): HK<F, HK<G, A>> = this as HK<F, HK<G, A>>

@Suppress("UNCHECKED_CAST")
fun <F, G, A, B> HK2<ComposedType<F, G>, A, B>.lowerB(): HK2<F, HK2<G, A, B>, HK2<G, A, B>> = this as HK2<F, HK2<G, A, B>, HK2<G, A, B>>

interface ComposedFoldable<F, G> :
        Foldable<ComposedType<F, G>> {

    fun FF(): Foldable<F>

    fun GF(): Foldable<G>

    override fun <A, B> foldL(fa: HK<ComposedType<F, G>, A>, b: B, f: (B, A) -> B): B =
            FF().foldL(fa.lower(), b, { bb, aa -> GF().foldL(aa, bb, f) })

    fun <A, B> foldLC(fa: HK<F, HK<G, A>>, b: B, f: (B, A) -> B): B = foldL(fa.lift(), b, f)

    override fun <A, B> foldR(fa: HK<ComposedType<F, G>, A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
            FF().foldR(fa.lower(), lb, { laa, lbb -> GF().foldR(laa, lbb, f) })

    fun <A, B> foldRC(fa: HK<F, HK<G, A>>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> = foldR(fa.lift(), lb, f)

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

interface ComposedTraverse<F, G> :
        Traverse<ComposedType<F, G>>,
        ComposedFoldable<F, G> {

    fun FT(): Traverse<F>

    fun GT(): Traverse<G>

    fun GA(): Applicative<G>

    override fun FF(): Foldable<F> = FT()

    override fun GF(): Foldable<G> = GT()

    override fun <H, A, B> traverse(fa: HK<ComposedType<F, G>, A>, f: (A) -> HK<H, B>, HA: Applicative<H>): HK<H, HK<ComposedType<F, G>, B>> =
            HA.map(FT().traverse(fa.lower(), { ga -> GT().traverse(ga, f, HA) }, HA), { it.lift() })

    fun <H, A, B> traverseC(fa: HK<F, HK<G, A>>, f: (A) -> HK<H, B>, HA: Applicative<H>): HK<H, HK<ComposedType<F, G>, B>> = traverse(fa.lift(), f, HA)

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

inline fun <reified F, reified G> Traverse<F>.compose(GT: Traverse<G> = traverse<G>(), GA: Applicative<G> = applicative<G>()): Traverse<ComposedType<F, G>> =
        object :
                ComposedTraverse<F, G> {
            override fun FT(): Traverse<F> = this@compose

            override fun GT(): Traverse<G> = GT

            override fun GA(): Applicative<G> = GA
        }

interface ComposedSemigroupK<F, G> : SemigroupK<ComposedType<F, G>> {

    fun F(): SemigroupK<F>

    override fun <A> combineK(x: HK<ComposedType<F, G>, A>, y: HK<ComposedType<F, G>, A>): HK<ComposedType<F, G>, A> = F().combineK(x.lower(), y.lower()).lift()

    fun <A> combineKC(x: HK<F, HK<G, A>>, y: HK<F, HK<G, A>>): HK<ComposedType<F, G>, A> = combineK(x.lift(), y.lift())

    companion object {
        operator fun <F, G> invoke(SF: SemigroupK<F>): SemigroupK<ComposedType<F, G>> =
                object : ComposedSemigroupK<F, G> {
                    override fun F(): SemigroupK<F> = SF
                }
    }
}

inline fun <F, G> SemigroupK<F>.compose(): SemigroupK<ComposedType<F, G>> = object : ComposedSemigroupK<F, G> {
    override fun F(): SemigroupK<F> = this@compose
}

interface ComposedMonoidK<F, G> : MonoidK<ComposedType<F, G>>, ComposedSemigroupK<F, G> {

    override fun F(): MonoidK<F>

    override fun <A> empty(): HK<ComposedType<F, G>, A> = F().empty<HK<G, A>>().lift()

    fun <A> emptyC(): HK<F, HK<G, A>> = empty<A>().lower()

    companion object {
        operator fun <F, G> invoke(MK: MonoidK<F>): MonoidK<ComposedType<F, G>> =
                object : ComposedMonoidK<F, G> {
                    override fun F(): MonoidK<F> = MK
                }
    }
}

fun <F, G> MonoidK<F>.compose(): MonoidK<ComposedType<F, G>> = object : ComposedMonoidK<F, G> {
    override fun F(): MonoidK<F> = this@compose
}

interface ComposedFunctor<F, G> : Functor<ComposedType<F, G>> {
    fun F(): Functor<F>

    fun G(): Functor<G>

    override fun <A, B> map(fa: HK<ComposedType<F, G>, A>, f: (A) -> B): HK<ComposedType<F, G>, B> = F().map(fa.lower(), { G().map(it, f) }).lift()

    fun <A, B> mapC(fa: HK<F, HK<G, A>>, f: (A) -> B): HK<F, HK<G, B>> = map(fa.lift(), f).lower()

    companion object {
        operator fun <F, G> invoke(FF: Functor<F>, GF: Functor<G>): Functor<ComposedType<F, G>> =
                object : ComposedFunctor<F, G> {
                    override fun F(): Functor<F> = FF

                    override fun G(): Functor<G> = GF
                }
    }
}

inline fun <reified F, reified G> Functor<F>.compose(GF: Functor<G>): Functor<ComposedType<F, G>> = ComposedFunctor(this, GF)

interface ComposedApplicative<F, G> : Applicative<ComposedType<F, G>>, ComposedFunctor<F, G> {
    override fun F(): Applicative<F>

    override fun G(): Applicative<G>

    override fun <A, B> map(fa: HK<ComposedType<F, G>, A>, f: (A) -> B): HK<ComposedType<F, G>, B> = ap(fa, pure(f))

    override fun <A> pure(a: A): HK<ComposedType<F, G>, A> = F().pure(G().pure(a)).lift()

    override fun <A, B> ap(fa: HK<ComposedType<F, G>, A>, ff: HK<ComposedType<F, G>, (A) -> B>):
            HK<ComposedType<F, G>, B> = F().ap(fa.lower(), F().map(ff.lower(), { gfa: HK<G, (A) -> B> -> { ga: HK<G, A> -> G().ap(ga, gfa) } })).lift()

    fun <A, B> apC(fa: HK<F, HK<G, A>>, ff: HK<F, HK<G, (A) -> B>>): HK<F, HK<G, B>> = ap(fa.lift(), ff.lift()).lower()

    companion object {
        operator fun <F, G> invoke(FF: Applicative<F>, GF: Applicative<G>)
                : Applicative<ComposedType<F, G>> =
                object : ComposedApplicative<F, G> {
                    override fun F(): Applicative<F> = FF

                    override fun G(): Applicative<G> = GF
                }
    }
}

inline fun <reified F, reified G> Applicative<F>.compose(GA: Applicative<G> = applicative<G>()): Applicative<ComposedType<F, G>> = ComposedApplicative(this, GA)

interface ComposedAlternative<F, G> : Alternative<ComposedType<F, G>>, ComposedApplicative<F, G>, ComposedMonoidK<F, G> {
    override fun F(): Alternative<F>

    companion object {
        operator fun <F, G> invoke(AF: Alternative<F>, AG: Applicative<G>)
                : Alternative<ComposedType<F, G>> =
                object : ComposedAlternative<F, G> {
                    override fun F(): Alternative<F> = AF

                    override fun G(): Applicative<G> = AG
                }
    }
}

inline fun <reified F, reified G> Alternative<F>.compose(GA: Applicative<G> = applicative<G>()): Alternative<ComposedType<F, G>> = ComposedAlternative(this, GA)

interface ComposedFunctorFilter<F, G> : FunctorFilter<ComposedType<F, G>>, ComposedFunctor<F, G> {

    override fun F(): Functor<F>

    override fun G(): FunctorFilter<G>

    override fun <A, B> mapFilter(fga: HK<ComposedType<F, G>, A>, f: (A) -> Option<B>): HK<ComposedType<F, G>, B> =
            F().map(fga.lower(), { G().mapFilter(it, f) }).lift()

    fun <A, B> mapFilterC(fga: HK<F, HK<G, A>>, f: (A) -> Option<B>): HK<F, HK<G, B>> =
            mapFilter(fga.lift(), f).lower()

    companion object {
        operator fun <F, G> invoke(FF: Functor<F>, FFG: FunctorFilter<G>): ComposedFunctorFilter<F, G> =
                object : ComposedFunctorFilter<F, G> {
                    override fun F(): Functor<F> = FF

                    override fun G(): FunctorFilter<G> = FFG
                }
    }
}

inline fun <reified F, reified G> Functor<F>.composeFilter(FFG: FunctorFilter<G> = functorFilter()):
        FunctorFilter<ComposedType<F, G>> = ComposedFunctorFilter(this, FFG)

interface ComposedBifoldable<F, G> : Bifoldable<ComposedType<F, G>> {
    fun F(): Bifoldable<F>

    fun G(): Bifoldable<G>

    override fun <A, B, C> bifoldLeft(fab: HK2<ComposedType<F, G>, A, B>, c: C, f: (C, A) -> C, g: (C, B) -> C): C =
            F().bifoldLeft(fab.lowerB(), c,
                    { cc: C, gab: HK2<G, A, B> -> G().bifoldLeft(gab, cc, f, g) },
                    { cc: C, gab: HK2<G, A, B> -> G().bifoldLeft(gab, cc, f, g) })

    override fun <A, B, C> bifoldRight(fab: HK2<ComposedType<F, G>, A, B>, c: Eval<C>, f: (A, Eval<C>) -> Eval<C>, g: (B, Eval<C>) -> Eval<C>): Eval<C> =
            F().bifoldRight(fab.lowerB(), c,
                    { gab: HK2<G, A, B>, cc: Eval<C> -> G().bifoldRight(gab, cc, f, g) },
                    { gab: HK2<G, A, B>, cc: Eval<C> -> G().bifoldRight(gab, cc, f, g) })

    fun <A, B, C> bifoldLeftC(fab: HK2<F, HK2<G, A, B>, HK2<G, A, B>>, c: C, f: (C, A) -> C, g: (C, B) -> C): C =
            bifoldLeft(fab.liftB(), c, f, g)

    fun <A, B, C> bifoldRightC(fab: HK2<F, HK2<G, A, B>, HK2<G, A, B>>, c: Eval<C>, f: (A, Eval<C>) -> Eval<C>, g: (B, Eval<C>) -> Eval<C>): Eval<C> =
            bifoldRight(fab.liftB(), c, f, g)

    companion object {
        operator fun <F, G> invoke(BF: Bifoldable<F>, BG: Bifoldable<G>): ComposedBifoldable<F, G> =
                object : ComposedBifoldable<F, G> {
                    override fun F(): Bifoldable<F> = BF

                    override fun G(): Bifoldable<G> = BG
                }
    }
}

inline fun <reified F, reified G> Bifoldable<F>.compose(BG: Bifoldable<G> = bifoldable()): Bifoldable<ComposedType<F, G>> = ComposedBifoldable(this, BG)
