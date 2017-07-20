package kategory

interface CoproductComonad<F, G> : Comonad<CoproductFG<F, G>> {

    override fun <A, B> coflatMap(fa: CoproductKind<F, G, A>, f: (CoproductKind<F, G, A>) -> B): Coproduct<F, G, B> =
            fa.ev().coflatMap(f)

    override fun <A> extract(fa: CoproductKind<F, G, A>): A =
            fa.ev().extract()

    override fun <A, B> map(fa: HK<CoproductFG<F, G>, A>, f: (A) -> B): CoproductKind<F, G, B> =
            fa.ev().map(f)

    companion object {
        // Cobinding for HK2 requires an instance to infer the types.
        // As cobinding cannot be delegated you have to create an <Any, Any> so any internal type can be used
        fun any(): CoproductComonad<Any, Any> =
                object : CoproductComonad<Any, Any> {}
    }
}

interface CoproductFunctor<F, G> : Functor<CoproductFG<F, G>> {
    override fun <A, B> map(fa: HK<CoproductFG<F, G>, A>, f: (A) -> B): HK<CoproductFG<F, G>, B> =
            fa.ev().map(f)
}

interface CoproductTraverse<F, G> : Traverse<CoproductFG<F, G>> {

    fun FF(): Traverse<F>

    fun FG(): Traverse<G>

    override fun <H, A, B> traverse(fa: HK<CoproductFG<F, G>, A>, f: (A) -> HK<H, B>, GA: Applicative<H>): HK<H, HK<CoproductFG<F, G>, B>> =
            fa.ev().traverse(f, GA, FF(), FG())

    override fun <A, B> foldL(fa: HK<CoproductFG<F, G>, A>, b: B, f: (B, A) -> B): B =
            fa.ev().foldL(b, f, FF(), FG())

    override fun <A, B> foldR(fa: HK<CoproductFG<F, G>, A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
            fa.ev().foldR(lb, f, FF(), FG())

}