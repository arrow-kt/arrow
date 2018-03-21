package arrow.typeclasses

interface Monoid<A> : Semigroup<A> {
    /**
     * A zero value for this A
     */
    fun empty(): A

    /**
     * Combine a collection of [A] values.
     */
    fun combineAll(elems: Collection<A>): A =
            if (elems.isEmpty()) empty() else elems.reduce { a, b -> combine(a, b) }

}

/**
 * Combine an array of [A] values.
 */
fun <A> Monoid<A>.combineAll(vararg elems: A): A = combineAll(elems.asList())