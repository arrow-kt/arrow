package kategory

interface Semigroup<A> : Typeclass {
    /**
     * Combine two [A] values.
     */
    fun combine(a: A, b: A): A

}

inline fun <reified A> A.combine(FT: Semigroup<A> = semigroup(), b: A): A = FT.combine(this, b)

inline fun <reified A> semigroup(): Semigroup<A> = instance(InstanceParametrizedType(Semigroup::class.java, listOf(A::class.java)))
