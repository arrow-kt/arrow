package katz

class IorTraverse<A> : Traverse<HK<Ior.F, A>> {
    override fun <G, B, C> traverse(fa: HK<HK<Ior.F, A>, B>, f: (B) -> HK<G, C>, GA: Applicative<G>): HK<G, HK<HK<Ior.F, A>, C>> =
            fa.ev().fold({ GA.pure(Ior.Left(it)) }, { GA.map(f(it), { Ior.Right(it) }) }, { a, b -> GA.map(f(b), { Ior.Right(it) }) })

    override fun <B, C> foldL(fa: HK<HK<Ior.F, A>, B>, c: C, f: (C, B) -> C): C =
            fa.ev().fold({ c }, { f(c, it) }, { _, b -> f(c, b) })

    override fun <B, C> foldR(fa: HK<HK<Ior.F, A>, B>, lc: Eval<C>, f: (B, Eval<C>) -> Eval<C>): Eval<C> =
            fa.ev().fold({ lc }, { f(it, lc) }, { _, b -> f(b, lc) })
}
