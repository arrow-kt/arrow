package arrow.typeclasses

import arrow.core.Option

/**
 * ank_macro_hierarchy(arrow.typeclasses.Semiring)
 */
interface Semiring<A> : Monoid<A> {

    /**
     * A zero value for this A
     */
    fun zero(): A

    /**
     * A one value for this A
     */
    fun one(): A

    /**
     * Multiplicatively combine two [A] values.
     */
    fun A.combineMultiplicate(b: A): A

    override fun empty(): A = one()

    operator fun A.times(b: A): A =
            this.combineMultiplicate(b)

    /**
     * Maybe multiplicatively combine two [A] values.
     */
    fun A.maybeCombineMultiplicate(b: A?): A =
            Option.fromNullable(b).fold({ this }, { combineMultiplicate(it) })
}