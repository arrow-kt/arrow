package arrow

/**
 * MonoidK is a universal monoid which operates on kinds.
 *
 * MonoidK<F> allows two F<A> values to be combined, for any A. It also means that for any A, there
 * is an "empty" F<A> value.
 */
interface MonoidK<F> : SemigroupK<F>, Typeclass {

    /**
     * Given a type A, create an "empty" F<A> value.
     */
    fun <A> empty(): HK<F, A>

    override fun <A> algebra(): Monoid<HK<F, A>> = object : Monoid<HK<F, A>> {

        override fun empty(): HK<F, A> = this@MonoidK.empty()

        override fun combine(a: HK<F, A>, b: HK<F, A>): HK<F, A> = this@MonoidK.combineK(a, b)
    }
}

inline fun <reified F> monoidK(): MonoidK<F> = instance(InstanceParametrizedType(MonoidK::class.java, listOf(typeLiteral<F>())))
