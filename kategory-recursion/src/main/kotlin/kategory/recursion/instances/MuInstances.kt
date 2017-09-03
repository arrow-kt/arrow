package kategory

interface MuInstances<F> : Birecursive<MuHK, F> {
    fun FG(): Functor<F>

    override fun projectT(fg: MuKind<F>): HK<Nested<MuHK, F>, MuKind<F>> =
            fg.ev().projectT()

    override fun embedT(compFG: HK<Nested<MuHK, F>, MuKind<F>>): MuKind<F> =
            Mu.embedT(compFG, FG())
}