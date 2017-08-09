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

    override fun <A, B> foldR(fa: HK<ComposedType<F, G>, A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> = FF.foldR(fa.lower(), lb, { laa, lbb -> GF.foldR(laa, lbb, f) })

    fun <A, B> foldRC(fa: HK<F, HK<G, A>>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> = foldR(fa.lift(), lb, f)

    companion object {
        inline operator fun <reified F, reified G> invoke(FF: Foldable<F> = foldable<F>(), GF: Foldable<G> = foldable<G>()): ComposedFoldable<F, G> = ComposedFoldable(FF, GF)
    }
}

inline fun <F, reified G> Foldable<F>.compose(GT: Foldable<G> = foldable<G>()): ComposedFoldable<F, G> = ComposedFoldable(this, GT)

data class ComposedTraverse<F, G>(val FT: Traverse<F>, val GT: Traverse<G>, val GA: Applicative<G>, val CF: ComposedFoldable<F, G> = ComposedFoldable(FT, GT))
    : Traverse<ComposedType<F, G>>, Foldable<ComposedType<F, G>> by CF {

    override fun <H, A, B> traverse(fa: HK<ComposedType<F, G>, A>, f: (A) -> HK<H, B>, HA: Applicative<H>): HK<H, HK<ComposedType<F, G>, B>> = HA.map(FT.traverse(fa.lower(), { ga -> GT.traverse(ga, f, HA) }, HA), { it.lift() })

    fun <H, A, B> traverseC(fa: HK<F, HK<G, A>>, f: (A) -> HK<H, B>, HA: Applicative<H>): HK<H, HK<ComposedType<F, G>, B>> = traverse(fa.lift(), f, HA)

    companion object {
        inline operator fun <reified F, reified G> invoke(FF: Traverse<F> = traverse<F>(), GF: Traverse<G> = traverse<G>(), GA: Applicative<G> = applicative<G>()): ComposedTraverse<F, G> = ComposedTraverse(FF, GF, GA)
    }
}

inline fun <F, reified G> Traverse<F>.compose(GT: Traverse<G> = traverse<G>(), GA: Applicative<G> = applicative<G>()): Traverse<ComposedType<F, G>> = ComposedTraverse(this, GT, GA)

interface ComposedSemigroupK<F, G> : SemigroupK<ComposedType<F, G>> {

    fun F(): SemigroupK<F>

    override fun <A> combineK(x: HK<ComposedType<F, G>, A>, y: HK<ComposedType<F, G>, A>): HK<ComposedType<F, G>, A> = F().combineK(x.lower(), y.lower()).lift()

    fun <A> combineKC(x: HK<F, HK<G, A>>, y: HK<F, HK<G, A>>): HK<ComposedType<F, G>, A> = combineK(x.lift(), y.lift())
}

inline fun <F, reified G> SemigroupK<F>.compose(): SemigroupK<ComposedType<F, G>> = object : ComposedSemigroupK<F, G> {
    override fun F(): SemigroupK<F> = this@compose
}

interface ComposedMonoidK<F, G> : MonoidK<ComposedType<F, G>>, ComposedSemigroupK<F, G> {

    override fun F(): MonoidK<F>

    override fun <A> empty(): HK<ComposedType<F, G>, A> = F().empty<HK<G, A>>().lift()
}

fun <F, G> MonoidK<F>.compose(): MonoidK<ComposedType<F, G>> = object : ComposedMonoidK<F, G> {
    override fun F(): MonoidK<F> = this@compose
}
