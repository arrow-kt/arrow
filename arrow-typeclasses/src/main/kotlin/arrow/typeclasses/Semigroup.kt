package arrow.typeclasses

import arrow.*
import arrow.core.Option

@typeclass
interface Semigroup<A> : Typeclass {
    /**
     * Combine two [A] values.
     */
    fun combine(a: A, b: A): A

    fun maybeCombine(a: A, b: A?): A = Option.fromNullable(b).fold({ a }, { this.combine(a, it) })

}
