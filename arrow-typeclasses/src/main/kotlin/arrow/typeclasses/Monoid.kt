package arrow

interface Monoid<A> : Semigroup<A>, Typeclass {
    /**
     * A zero value for this A
     */
    fun empty(): A

    /**
     * Combine an array of [A] values.
     */
    fun combineAll(vararg elems: A): A = combineAll(elems.asList())

    /**
     * Combine a collection of [A] values.
     */
    fun combineAll(elems: Collection<A>): A =
            if (elems.isEmpty()) empty() else elems.reduce { a, b -> combine(a, b) }

}

inline fun <reified A> monoid(): Monoid<A> = instance(InstanceParametrizedType(Monoid::class.java, listOf(typeLiteral<A>())))
