package arrow.typeclasses

import arrow.core.Option

interface Semigroup<A> {
    /**
     * Combine two [A] values.
     */
    fun A.combine(b: A): A

    fun A.maybeCombine(b: A?): A = Option.fromNullable(b).fold({ this }, { combine(it) })

}
