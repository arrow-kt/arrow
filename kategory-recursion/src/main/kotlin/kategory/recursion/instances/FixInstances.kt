package kategory

interface FixInstances<F> : Birecursive<FixHK, F> {
    fun FG(): Functor<F>

    override fun projectT(fg: HK<FixHK, F>): HK<Nested<FixHK, F>, FixKind<F>> =
            fg.ev().projectT()

    override fun embedT(compFG: HK<Nested<Nested<FixHK, F>, FixHK>, F>): FixKind<F> =
            Fix.embedT(compFG, FG())
}