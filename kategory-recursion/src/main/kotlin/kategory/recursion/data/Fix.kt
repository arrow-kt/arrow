package kategory

@higherkind data class Fix<F>(val FF: Functor<F>, val unfix: Fix<HK<F, HK<FixHK, F>>>) : FixKind<F> {

    fun projectT(): HK<Nested<FixHK, F>, FixKind<F>> = unfix.nest()

    companion object {
        inline fun <reified F> embedT(compFG: HK<Nested<Nested<FixHK, F>, FixHK>, F>, FF: Functor<F> = functor<F>()): FixKind<F> =
                Fix(FF, compFG.unnest().unnest().ev())

        inline fun <F> instances(FF: Functor<F>): FixInstances<F> = object : FixInstances<F> {
            override fun FG(): Functor<F> = FF
        }

        inline fun <reified F> birecursive(FF: Functor<F> = functor<F>()): Birecursive<FixHK, F> = instances(FF)

        fun <F> birecursiveFix(FF: Functor<F>): Birecursive<FixHK, F> =
                algebraIso({ Fix(FF, it.unnest().ev()) }, { it.ev().unfix.nest() })
    }
}