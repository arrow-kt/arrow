package kategory

/**
 * https://www.youtube.com/watch?v=wvSP5qYiz4Y
 */
interface ComposedType<out F, out G>

@Suppress("UNCHECKED_CAST")
fun <F, G, A> HK<F, HK<G, A>>.lift(): HK<ComposedType<F, G>, A> = this as HK<ComposedType<F, G>, A>

@Suppress("UNCHECKED_CAST")
fun <F, G, A> HK<ComposedType<F, G>, A>.lower(): HK<F, HK<G, A>> = this as HK<F, HK<G, A>>

data class ComposedFoldable<in F, in G>(val FF: Foldable<F>, val GF: Foldable<G>) : Foldable<ComposedType<F, G>> {
    override fun <A, B> foldL(fa: HK<ComposedType<F, G>, A>, b: B, f: (B, A) -> B): B = FF.foldL(fa.lower(), b, { bb, aa -> GF.foldL(aa, bb, f) })

    fun <A, B> foldLC(fa: HK<F, HK<G, A>>, b: B, f: (B, A) -> B): B = foldL(fa.lift(), b, f)

    override fun <A, B> foldR(fa: HK<ComposedType<F, G>, A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
            FF.foldR(fa.lower(), lb, { laa, lbb -> GF.foldR(laa, lbb, f) })

    fun <A, B> foldRC(fa: HK<F, HK<G, A>>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> = foldR(fa.lift(), lb, f)

    companion object {
        inline operator fun <reified F, reified G> invoke(FF: Foldable<F> = foldable<F>(), GF: Foldable<G> = foldable<G>()): ComposedFoldable<F, G> =
                ComposedFoldable(FF, GF)
    }
}

inline fun <F, reified G> Foldable<F>.compose(GT: Foldable<G> = foldable<G>()): ComposedFoldable<F, G> = ComposedFoldable(this, GT)

data class ComposedTraverse<F, G>(val FT: Traverse<F>, val GT: Traverse<G>, val GA: Applicative<G>, val CF: ComposedFoldable<F, G> = ComposedFoldable(FT, GT))
    : Traverse<ComposedType<F, G>>, Foldable<ComposedType<F, G>> by CF {

    override fun <H, A, B> traverse(fa: HK<ComposedType<F, G>, A>, f: (A) -> HK<H, B>, HA: Applicative<H>): HK<H, HK<ComposedType<F, G>, B>> =
            HA.map(FT.traverse(fa.lower(), { ga -> GT.traverse(ga, f, HA) }, HA), { it.lift() })

    fun <H, A, B> traverseC(fa: HK<F, HK<G, A>>, f: (A) -> HK<H, B>, HA: Applicative<H>): HK<H, HK<ComposedType<F, G>, B>> = traverse(fa.lift(), f, HA)

    companion object {
        inline operator fun <reified F, reified G> invoke(FF: Traverse<F> = traverse<F>(),
                                                          GF: Traverse<G> = traverse<G>(),
                                                          GA: Applicative<G> = applicative<G>()): ComposedTraverse<F, G> = ComposedTraverse(FF, GF, GA)
    }
}

inline fun <F, reified G> Traverse<F>.compose(GT: Traverse<G> = traverse<G>(), GA: Applicative<G> = applicative<G>()): Traverse<ComposedType<F, G>> =
        ComposedTraverse(this, GT, GA)

data class ComposedReducible<F, G>(val RF: Reducible<F>, val RG: Reducible<G>, val FT: Traverse<F>, val GT: Traverse<G>, val CF: ComposedFoldable<F, G> =
ComposedFoldable(FT, GT))
    : Reducible<ComposedType<F, G>>, Foldable<ComposedType<F, G>> by CF {

    override fun <A, B> reduceLeftTo(fa: HK<ComposedType<F, G>, A>, f: (A) -> B, g: (B, A) -> B): B =
            RF.reduceLeftTo(fa.lower(), { ga: HK<G, A> -> RG.reduceLeftTo(ga, f, g) }) { b, ga ->
                RG.foldL(ga, b, g)
            }

    override fun <A, B> reduceRightTo(fa: HK<ComposedType<F, G>, A>, f: (A) -> B, g: (A, Eval<B>) -> Eval<B>): Eval<B> =
            RF.reduceRightTo(fa.lower(), { ga: HK<G, A> -> RG.reduceRightTo(ga, f, g).value() }) { ga, lb ->
                RG.foldR(ga, lb, g)
            }
}

inline fun <reified F, reified G> Reducible<F>.compose(RG: Reducible<G> = reducible<G>(),
                                                       FT: Traverse<F> = traverse<F>(), GT: Traverse<G> = traverse<G>(),
                                                       GA: Applicative<G> = applicative<G>()): Reducible<ComposedType<F, G>> =
        ComposedReducible(this, RG, FT, GT)

interface ComposedSemigroupK<F, G> : SemigroupK<ComposedType<F, G>> {

    fun F(): SemigroupK<F>

    override fun <A> combineK(x: HK<ComposedType<F, G>, A>, y: HK<ComposedType<F, G>, A>): HK<ComposedType<F, G>, A> = F().combineK(x.lower(), y.lower()).lift()

    fun <A> combineKC(x: HK<F, HK<G, A>>, y: HK<F, HK<G, A>>): HK<F, HK<G, A>> = combineK(x.lift(), y.lift()).lower()

    companion object {
        inline operator fun <reified F, G> invoke(FF: SemigroupK<F> = monoidK<F>()): ComposedSemigroupK<F, G> = object : ComposedSemigroupK<F, G> {
            override fun F(): SemigroupK<F> = FF
        }
    }
}

inline fun <reified F, G> SemigroupK<F>.compose(): SemigroupK<ComposedType<F, G>> = ComposedSemigroupK(this)

interface ComposedMonoidK<F, G> : MonoidK<ComposedType<F, G>>, ComposedSemigroupK<F, G> {

    override fun F(): MonoidK<F>

    override fun <A> empty(): HK<ComposedType<F, G>, A> = F().empty<HK<G, A>>().lift()

    companion object {
        inline operator fun <reified F, G> invoke(FF: MonoidK<F> = monoidK<F>()): ComposedMonoidK<F, G> = object : ComposedMonoidK<F, G> {
            override fun F(): MonoidK<F> = FF
        }
    }
}

inline fun <reified F, G> MonoidK<F>.compose(): MonoidK<ComposedType<F, G>> = ComposedMonoidK(this)

interface ComposedFunctor<F, G> : Functor<ComposedType<F, G>> {
    fun F(): Functor<F>

    fun G(): Functor<G>

    override fun <A, B> map(fa: HK<ComposedType<F, G>, A>, f: (A) -> B): HK<ComposedType<F, G>, B> = F().map(fa.lower(), { G().map(it, f) }).lift()

    fun <A, B> mapC(fa: HK<F, HK<G, A>>, f: (A) -> B): HK<F, HK<G, B>> = map(fa.lift(), f).lower()

    companion object {
        inline operator fun <reified F, reified G> invoke(FF: Functor<F> = functor<F>(), GF: Functor<G> = functor<G>()): Functor<ComposedType<F, G>> =
                object : ComposedFunctor<F, G> {
                    override fun F(): Functor<F> = FF

                    override fun G(): Functor<G> = GF
                }
    }
}

inline fun <reified F, reified G> Functor<F>.compose(GF: Functor<G> = functor<G>()): Functor<ComposedType<F, G>> = ComposedFunctor(this, GF)

interface ComposedApplicative<F, G> : Applicative<ComposedType<F, G>>, ComposedFunctor<F, G> {
    override fun F(): Applicative<F>

    override fun G(): Applicative<G>

    override fun <A, B> map(fa: HK<ComposedType<F, G>, A>, f: (A) -> B): HK<ComposedType<F, G>, B> = ap(fa, pure(f))

    override fun <A> pure(a: A): HK<ComposedType<F, G>, A> = F().pure(G().pure(a)).lift()

    override fun <A, B> ap(fa: HK<ComposedType<F, G>, A>, ff: HK<ComposedType<F, G>, (A) -> B>):
            HK<ComposedType<F, G>, B> = F().ap(fa.lower(), F().map(ff.lower(), { gfa: HK<G, (A) -> B> -> { ga: HK<G, A> -> G().ap(ga, gfa) } })).lift()

    fun <A, B> apC(fa: HK<F, HK<G, A>>, ff: HK<F, HK<G, (A) -> B>>): HK<F, HK<G, B>> = ap(fa.lift(), ff.lift()).lower()

    companion object {
        inline operator fun <reified F, reified G> invoke(FF: Applicative<F> = applicative<F>(), GF: Applicative<G> = applicative<G>())
                : Applicative<ComposedType<F, G>> =
                object : ComposedApplicative<F, G> {
                    override fun F(): Applicative<F> = FF

                    override fun G(): Applicative<G> = GF
                }
    }
}

inline fun <reified F, reified G> Applicative<F>.compose(GA: Applicative<G> = applicative<G>()): Applicative<ComposedType<F, G>> = ComposedApplicative(this, GA)

interface ComposedFunctorFilter<F, G> : FunctorFilter<ComposedType<F, G>>, ComposedFunctor<F, G> {

    override fun F(): Functor<F>
    override fun G(): FunctorFilter<G>

    override fun <A, B> mapFilter(fga: HK<ComposedType<F, G>, A>, f: (A) -> Option<B>): HK<ComposedType<F, G>, B> =
            F().map(fga.lower(), { G().mapFilter(it, f) }).lift()

    companion object {
        inline operator fun <reified F, reified G> invoke(FF: Functor<F> = functor(), FFG: FunctorFilter<G> = functorFilter()): ComposedFunctorFilter<F, G> =
                object : ComposedFunctorFilter<F, G> {
                    override fun F(): Functor<F> = FF

                    override fun G(): FunctorFilter<G> = FFG
                }
    }
}

inline fun <reified F, reified G> Functor<F>.composeFilter(FFG: FunctorFilter<G> = functorFilter()):
        FunctorFilter<ComposedType<F, G>> = ComposedFunctorFilter(this, FFG)
