package kategory

@higherkind data class Fix<F>(val FF: Functor<F>, val unfix: Fix<HK<F, HK<FixHK, F>>>) : FixKind<F> {

    fun projectT(): HK<Nested<FixHK, F>, FixKind<F>> = unfix.nest()

    companion object {
        inline fun <reified F> embedT(compFG: NestedType<Nested<FixHK, F>, FixHK, F>, dummy: Unit = Unit, FF: Functor<F> = functor<F>()): FixKind<F> =
                embedT(compFG, FF)

        inline fun <F> embedT(compFG: NestedType<Nested<FixHK, F>, FixHK, F>, FF: Functor<F>): FixKind<F> =
                Fix(FF, compFG.unnest().unnest().ev())

        inline fun <F> instances(FF: Functor<F>): FixInstances<F> = object : FixInstances<F> {
            override fun FG(): Functor<F> = FF
        }

        inline fun <reified F> birecursive(FF: Functor<F> = functor<F>()): Birecursive<FixHK, F> = instances(FF)
    }
}