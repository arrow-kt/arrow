package arrow.recursion

import arrow.Kind
import arrow.higherkind
import arrow.typeclasses.*

@higherkind data class Fix<F>(val FF: Functor<F>, val unfix: Fix<Kind<F, Kind<ForFix, F>>>) : FixKind<F> {

    fun projectT(): Kind<Nested<ForFix, F>, FixKind<F>> = unfix.nest()

    companion object {
        inline fun <reified F> embedT(compFG: NestedType<Nested<ForFix, F>, ForFix, F>, dummy: Unit = Unit, FF: Functor<F> = functor<F>()): FixKind<F> =
                embedT(compFG.unnest(), FF)

        inline fun <F> embedT(compFG: Kind<Nested<ForFix, F>, FixKind<F>>, FF: Functor<F>): FixKind<F> =
                Fix(FF, compFG.unnest().reify())

    }
}