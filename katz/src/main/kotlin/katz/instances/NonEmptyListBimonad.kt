package katz

interface NonEmptyListBimonad : Bimonad<NonEmptyList.F>, NonEmptyListMonad, NonEmptyListComonad {
    override fun <A, B> map(fa: NonEmptyListKind<A>, f: (A) -> B): NonEmptyList<B> =
            fa.ev().map(f)
}
