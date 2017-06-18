package kategory

data class OptionTTraverse<F>(val FF: Traverse<F>, val MF: Monad<F>) : Traverse<OptionTF<F>> {
    override fun <G, A, B> traverse(fa: HK<OptionTF<F>, A>, f: (A) -> HK<G, B>, GA: Applicative<G>): HK<G, HK<OptionTF<F>, B>> =
            fa.ev().traverse(f, GA, FF, MF)

    override fun <A, B> foldL(fa: HK<OptionTF<F>, A>, b: B, f: (B, A) -> B): B =
            fa.ev().foldL(b, f, FF)

    override fun <A, B> foldR(fa: HK<OptionTF<F>, A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
            fa.ev().foldR(lb, f, FF)
}