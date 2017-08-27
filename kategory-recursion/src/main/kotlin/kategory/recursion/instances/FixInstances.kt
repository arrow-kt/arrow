package kategory

interface FixInstances<F> : Birecursive<FixHK, F> {
    override fun projectT(fg: HK<FixHK, F>): HK<ComposedType<FixHK, F>, FixKind<F>> =
            fg.ev().projectT()

    override fun embedT(compFG: HK<ComposedType<ComposedType<FixHK, F>, FixHK>, F>): FixKind<F> =
            Fix(FG(), compFG.lower().lower().ev())
}