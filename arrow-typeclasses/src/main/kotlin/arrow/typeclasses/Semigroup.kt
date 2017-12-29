package arrow.typeclasses

import arrow.InstanceParametrizedType
import arrow.Typeclass
import arrow.core.Option
import arrow.instance
import arrow.typeLiteral

interface Semigroup<A> : Typeclass {
    /**
     * Combine two [A] values.
     */
    fun combine(a: A, b: A): A

    fun maybeCombine(a: A, b: A?): A = Option.fromNullable(b).fold({ a }, { this.combine(a, it) })

}

inline fun <reified A> semigroup(): Semigroup<A> = instance(InstanceParametrizedType(Semigroup::class.java, listOf(typeLiteral<A>())))
