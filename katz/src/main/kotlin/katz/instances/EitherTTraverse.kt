package katz

data class EitherTTraverse<F, A>(val FF: Traverse<F>, val MF: Monad<F>) : Traverse<EitherTF<F, A>> {
    override fun <G, B, C> traverse(fa: HK<EitherTF<F, A>, B>, f: (B) -> HK<G, C>, GA: Applicative<G>): HK<G, HK<EitherTF<F, A>, C>> =
            fa.ev().traverse(f, GA, FF, MF)

    override fun <B, C> foldL(fa: HK<EitherTF<F, A>, B>, b: C, f: (C, B) -> C): C =
            fa.ev().foldL(b, f, FF)

    override fun <B, C> foldR(fa: HK<EitherTF<F, A>, B>, lb: Eval<C>, f: (B, Eval<C>) -> Eval<C>): Eval<C> =
            fa.ev().foldR(lb, f, FF)
}