package katz

class CofreeComonad<S> : Comonad<CofreeF<S>>, Typeclass {
    override fun <A, B> coflatMap(fa: CofreeKind<S, A>, f: (CofreeKind<S, A>) -> B): Cofree<S, B> =
            fa.ev().coflatMap(f)

    override fun <A> extract(fa: CofreeKind<S, A>): A =
            fa.ev().extract()

    override fun <A, B> map(fa: CofreeKind<S, A>, f: (A) -> B): Cofree<S, B> =
            fa.ev().map(f)

    override fun <A> duplicate(fa: CofreeKind<S, A>): HK<CofreeF<S>, Cofree<S, A>> =
            fa.ev().duplicate()
}