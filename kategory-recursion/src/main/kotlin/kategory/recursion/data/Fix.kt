package kategory

@higherkind data class Fix<F>(val FF: Functor<F>, val unfix: Fix<HK<F, HK<FixHK, F>>>) : FixKind<F> {

    fun projectT(): HK<ComposedType<FixHK, F>, FixKind<F>> = unfix.lift()

    companion object {
        inline fun <reified F> embedT(compFG: HK<ComposedType<ComposedType<FixHK, F>, FixHK>, F>, FF: Functor<F> = functor<F>()): FixKind<F> =
                Fix(FF, compFG.lower().lower().ev())

    }
}