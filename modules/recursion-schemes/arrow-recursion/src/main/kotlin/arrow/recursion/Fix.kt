package arrow.recursion

import arrow.*
import arrow.typeclasses.*

@higherkind data class Fix<F>(val FF: Functor<F>, val unfix: Fix<HK<F, HK<FixHK, F>>>) : FixKind<F> {

    fun projectT(): HK<Nested<FixHK, F>, FixKind<F>> = unfix.nest()

    companion object {
        inline fun <reified F> embedT(compFG: NestedType<Nested<FixHK, F>, FixHK, F>, dummy: Unit = Unit, FF: Functor<F> = functor<F>()): FixKind<F> =
                embedT(compFG.unnest(), FF)

        inline fun <F> embedT(compFG: HK<Nested<FixHK, F>, FixKind<F>>, FF: Functor<F>): FixKind<F> =
                Fix(FF, compFG.unnest().ev())

    }
}