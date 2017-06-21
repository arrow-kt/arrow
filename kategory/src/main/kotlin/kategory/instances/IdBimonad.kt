package kategory

interface IdBimonad : Bimonad<Id.F>, IdMonad, IdComonad {
    override fun <A, B> map(fa: IdKind<A>, f: (A) -> B): Id<B> =
            fa.ev().map(f)
}