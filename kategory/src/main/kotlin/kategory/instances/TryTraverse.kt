package kategory

object TryTraverse : Traverse<Try.F> {
    override fun <G, A, B> traverse(fa: HK<Try.F, A>, f: (A) -> HK<G, B>, GA: Applicative<G>): HK<G, HK<Try.F, B>> =
            fa.ev().fold({ GA.pure(Try.raise(IllegalStateException())) }, { GA.map(f(it), { Try { it } }) })

    override fun <A, B> foldL(fa: HK<Try.F, A>, b: B, f: (B, A) -> B): B =
            fa.ev().fold({ b }, { f(b, it) })

    override fun <A, B> foldR(fa: HK<Try.F, A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
            fa.ev().fold({ lb }, { f(it, lb) })
}
