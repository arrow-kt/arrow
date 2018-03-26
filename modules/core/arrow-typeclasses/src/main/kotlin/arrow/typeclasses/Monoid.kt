package arrow.typeclasses

interface Monoid<A> : Semigroup<A> {
    /**
     * A zero value for this A
     */
    fun empty(): A

    /**
     * Combine an [Collection] of [A] values.
     */
    fun Collection<A>.combineAll(): A =
            if (isEmpty()) empty() else reduce { a, b -> a.combine(b) }

    /**
     * Combine an array of [A] values.
     */
    fun combineAll(vararg elems: A): A = elems.asList().combineAll()

}