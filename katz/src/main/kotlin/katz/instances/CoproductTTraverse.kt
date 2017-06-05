package katz

data class CoproductTraverse<F, G>(val FF: Traverse<F>, val FG: Traverse<G>) : Traverse<CoproductFG<F, G>> {
    override fun <H, A, B> traverse(fa: HK<CoproductFG<F, G>, A>, f: (A) -> HK<H, B>, GA: Applicative<H>): HK<H, HK<CoproductFG<F, G>, B>> =
            fa.ev().traverse(f, GA, FF, FG)

    override fun <A, B> foldL(fa: HK<CoproductFG<F, G>, A>, b: B, f: (B, A) -> B): B =
            fa.ev().foldL(b, f, FF, FG)

    override fun <A, B> foldR(fa: HK<CoproductFG<F, G>, A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
            fa.ev().foldR(lb, f, FF, FG)
}