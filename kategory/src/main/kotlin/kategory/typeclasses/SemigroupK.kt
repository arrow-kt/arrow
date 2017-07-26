package kategory

interface SemigroupK<F> : Typeclass {

    /**
     * Combine two F<A> values.
     */
    fun <A> combineK(x: HK<F, A>, y: HK<F, A>): HK<F, A>

    /**
     * Given a type A, create a concrete Semigroup<F<A>>.
     */
    fun <A> algebra(): Semigroup<HK<F, A>> = object : Semigroup<HK<F, A>> {
        override fun combine(x: HK<F, A>, y: HK<F, A>): HK<F, A> =
                combineK(x, y)
    }
}

/**
 * Dummy SemigroupK instance to be able to test laws.
 */
class OptionSemigroupK : SemigroupK<Option.F> {
    override fun <A> combineK(x: HK<Option.F, A>, y: HK<Option.F, A>): HK<Option.F, A> {
        return x.flatMap { y }
    }
}
