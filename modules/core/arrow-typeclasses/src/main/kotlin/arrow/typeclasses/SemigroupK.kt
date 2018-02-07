package arrow.typeclasses

import arrow.*

@typeclass
interface SemigroupK<F> : TC {

    /**
     * Combine two F<A> values.
     */
    fun <A> combineK(x: Kind<F, A>, y: Kind<F, A>): Kind<F, A>

    /**
     * Given a type A, create a concrete Semigroup<F<A>>.
     */
    fun <A> algebra(): Semigroup<Kind<F, A>> = object : Semigroup<Kind<F, A>> {
        override fun combine(a: Kind<F, A>, b: Kind<F, A>): Kind<F, A> =
                combineK(a, b)
    }
}