package kategory

interface Monoid<A> : Semigroup<A> {
    /**
     * A zero value for this A
     */
    fun empty(): A

    /**
     * Combine a collection of [A] values.
     */
    fun combineAll(elems: Collection<A>): A =
            elems.fold(empty(), { a, b -> combine(a, b) })

}

inline fun <reified A> A.empty(FT: Monoid<A> = monoid()): A = FT.empty()

inline fun <reified A> Collection<A>.combineAll(FT: Monoid<A> = monoid()): A = FT.combineAll(this)

inline fun <reified A> monoid(): Monoid<A> = instance(InstanceParametrizedType(Monoid::class.java, listOf(A::class.java)))
