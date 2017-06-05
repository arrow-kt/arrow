package katz

object IdTraverse : Traverse<Id.F> {
    override fun <A, B> foldL(fa: HK<Id.F, A>, b: B, f: (B, A) -> B): B =
            f(b, fa.ev().value)

    override fun <A, B> foldR(fa: HK<Id.F, A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
            f(fa.ev().value, lb)

    override fun <G, A, B> traverse(fa: HK<Id.F, A>, f: (A) -> HK<G, B>, GA: Applicative<G>): HK<G, HK<Id.F, B>> =
            GA.map(f(fa.ev().value), { Id(it) })
}
