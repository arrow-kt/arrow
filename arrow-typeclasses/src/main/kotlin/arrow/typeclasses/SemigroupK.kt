package arrow.typeclasses

import arrow.*

interface SemigroupK<F> : Typeclass {

    /**
     * Combine two F<A> values.
     */
    fun <A> combineK(x: HK<F, A>, y: HK<F, A>): HK<F, A>

    /**
     * Given a type A, create a concrete Semigroup<F<A>>.
     */
    fun <A> algebra(): Semigroup<HK<F, A>> = object : Semigroup<HK<F, A>> {
        override fun combine(a: HK<F, A>, b: HK<F, A>): HK<F, A> =
                combineK(a, b)
    }
}

inline fun <reified F> semigroupK(): SemigroupK<F> = instance(InstanceParametrizedType(SemigroupK::class.java, listOf(typeLiteral<F>())))
