package kategory

interface Monoid<A> : Semigroup<A>, Typeclass {
    /**
     * A zero value for this A
     */
    fun empty(): A

}

inline fun <reified A> A.empty(FT: Monoid<A> = monoid()): A = FT.empty()

inline fun <reified A> monoid(): Monoid<A> = instance(InstanceParametrizedType(Monoid::class.java, listOf(typeLiteral<A>())))
