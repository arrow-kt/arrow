package katz

interface Monoid<A> : Semigroup<A> {
    /**
     * A zero value for this A
     */
    fun empty(): A

}

inline fun <reified A> monoid(): Monoid<A> =
        instance(InstanceParametrizedType(Monoid::class.java, listOf(A::class.java)))
