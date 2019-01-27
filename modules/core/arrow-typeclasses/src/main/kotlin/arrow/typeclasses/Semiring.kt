package arrow.typeclasses

import arrow.core.Option

/**
 * ank_macro_hierarchy(arrow.typeclasses.Semiring)
 */
interface Semiring<A> : Monoid<A> {

    fun zero(): A
    fun one(): A

    fun A.combineMultiplicate(b: A): A

    override fun empty(): A = one()

    operator fun A.times(b: A): A =
            this.combineMultiplicate(b)

    fun A.maybeProduct(b: A?): A =
            Option.fromNullable(b).fold({ this }, { combineMultiplicate(it) })
}