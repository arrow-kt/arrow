package katz

interface Semigroup<A> : Typeclass {
    /**
     * Combine two [A] values.
     */
    fun combine(a: A, b: A): A

    /**
     * Combine an array of [A] values.
     */
    fun combineAll(vararg elems: A): A = combineAll(elems.asList())

    /**
     * Combine a collection of [A] values.
     */
    fun combineAll(elems: Collection<A>): A = elems.reduce { a, b -> combine(a, b) }
}

inline fun <reified A> semigroup(): Semigroup<A> =
        instance(InstanceParametrizedType(Semigroup::class.java, listOf(A::class.java)))
