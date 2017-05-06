package katz

class CoproductComonad<F, G> : Comonad<CoproductFG<F, G>> {

    override fun <A, B> coflatMap(fa: CoproductKind<F, G, A>, f: (CoproductKind<F, G, A>) -> B): Coproduct<F, G, B> =
            fa.ev().coflatMap(f)

    override fun <A> extract(fa: CoproductKind<F, G, A>): A =
            fa.ev().extract()

    override fun <A, B> map(fa: HK<CoproductFG<F, G>, A>, f: (A) -> B): CoproductKind<F, G, B> =
            fa.ev().map(f)
}
