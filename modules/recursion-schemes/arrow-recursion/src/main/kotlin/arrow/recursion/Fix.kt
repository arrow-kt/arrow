package arrow.recursion

import arrow.Kind
import arrow.higherkind
import arrow.typeclasses.*

@higherkind
data class Fix<F>(val FF: Functor<F>, val unfix: Fix<Kind<F, Kind<ForFix, F>>>) : FixOf<F> {

    fun projectT(): Kind<Nested<ForFix, F>, FixOf<F>> = unfix.nest()

    companion object {
        inline fun <reified F> embedT(compFG: NestedType<Nested<ForFix, F>, ForFix, F>, dummy: Unit = Unit, FF: Functor<F>): FixOf<F> =
                embedT(compFG.unnest(), FF)

        inline fun <F> embedT(compFG: Kind<Nested<ForFix, F>, FixOf<F>>, FF: Functor<F>): FixOf<F> =
                Fix(FF, compFG.unnest().fix())

    }
}