package kategory

class CoproductComonad<F, G> : Comonad<CoproductFG<F, G>> {

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
                CoproductComonad()
    }
}
