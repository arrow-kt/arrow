package katz

interface ComposedType<F, G> {
    fun <A> apply(fhk: HK<ComposedType<F, G>, A>): HK<F, HK<G, A>>

    fun <A> unapply(fhk: HK<F, HK<G, A>>): HK<ComposedType<F, G>, A>
}

open class ComposedFoldable<F, G>(val CFG: ComposedType<F, G>, val FF: Foldable<F>, val GF: Foldable<G>) : Foldable<ComposedType<F, G>> {
    override fun <A, B> foldL(fa: HK<ComposedType<F, G>, A>, b: B, f: (B, A) -> B): B =
            FF.foldL(CFG.apply(fa), b, { bb, aa -> GF.foldL(aa, bb, f) })

    override fun <A, B> foldR(fa: HK<ComposedType<F, G>, A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
            FF.foldR(CFG.apply(fa), lb, { laa, lbb -> GF.foldR(laa, lbb, f) })
}

inline fun <F, reified G> Foldable<F>.compose(CGF: ComposedType<F, G>, GT: Foldable<G> = foldable<G>()) =
        ComposedFoldable(CGF, this, GT)

data class ComposedTraverse<F, G>(val CCFG: ComposedType<F, G>, val FT: Traverse<F>, val GT: Traverse<G>, val GA: Applicative<G>) : ComposedFoldable<F, G>(CCFG, FT, GT), Traverse<ComposedType<F, G>> {
    override fun <H, A, B> traverse(fa: HK<ComposedType<F, G>, A>, f: (A) -> HK<H, B>, HA: Applicative<H>): HK<H, HK<ComposedType<F, G>, B>> =
            HA.map(FT.traverse(CCFG.apply(fa), { ga -> GT.traverse(ga, f, HA) }, HA), CCFG::unapply)
}

inline fun <F, reified G> Traverse<F>.compose(CGF: ComposedType<F, G>, GT: Traverse<G> = traverse<G>(), GA: Applicative<G> = applicative<G>()) =
        ComposedTraverse(CGF, this, GT, GA)