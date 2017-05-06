package katz

interface IdComonad : IdMonad, Comonad<Id.F> {
    override fun <A, B> coflatMap(fa: IdKind<A>, f: (IdKind<A>) -> B): IdKind<B> =
            fa.ev().map({ f(fa) })

    override fun <A> extract(fa: IdKind<A>): A =
            fa.ev().value
}
