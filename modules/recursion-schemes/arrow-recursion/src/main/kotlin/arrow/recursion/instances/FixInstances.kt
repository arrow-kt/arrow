package arrow.recursion.instances

import arrow.*
import arrow.recursion.*
import arrow.recursion.typeclass.Birecursive
import arrow.typeclasses.Functor
import arrow.typeclasses.Nested

@instance(Fix::class)
interface FixInstances<F> : Birecursive<ForFix, F> {
    fun FF(): Functor<F>

    override fun projectT(fg: FixOf<F>): Kind<Nested<ForFix, F>, FixOf<F>> =
            fg.reify().projectT()

    override fun embedT(compFG: Kind<Nested<ForFix, F>, FixOf<F>>): FixOf<F> =
            Fix.embedT(compFG, FF())
}