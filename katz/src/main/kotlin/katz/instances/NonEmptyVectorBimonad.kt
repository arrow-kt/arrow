package katz

interface NonEmptyVectorBimonad : Bimonad<NonEmptyVector.F>, NonEmptyVectorMonad, NonEmptyVectorComonad {
    override fun <A, B> map(fa: NonEmptyVectorKind<A>, f: (A) -> B): NonEmptyVectorKind<B> =
            fa.ev().map(f)
}