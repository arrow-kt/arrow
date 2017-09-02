package kategory

interface FixInstances<F> : Birecursive<FixHK, F> {
    fun FG(): Functor<F>

    override fun projectT(fg: FixKind<F>): HK<Nested<FixHK, F>, FixKind<F>> =
            fg.ev().projectT()

    override fun embedT(compFG: HK<Nested<FixHK, F>, FixKind<F>>): FixKind<F> =
            Fix.embedT(compFG.nest(), FG())
}