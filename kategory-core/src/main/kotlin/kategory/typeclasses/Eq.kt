package kategory

/**
 * A type class used to determine equality between 2 instances of the same type [F] in a type safe way.
 */
interface Eq<in F> : Typeclass {

    /**
     * Compare two objects [a] and [b] of the type [F].
     *
     * @param a object to compare with [b]
     * @param b object to compare with [a]
     * @returns true if [a] and [b] are equivalent, false otherwise.
     */
    fun eqv(a: F, b: F): Boolean

    /** {@inh} */
    fun neqv(a: F, b: F): Boolean = !eqv(a, b)

    companion object {
        inline operator fun <F> invoke(crossinline feqv: (F, F) -> Boolean): Eq<F> = object : Eq<F> {
            override fun eqv(a: F, b: F): Boolean =
                    feqv(a, b)
        }

        fun any(): Eq<Any?> = EqAny

        object EqAny : Eq<Any?> {
            override fun eqv(a: Any?, b: Any?): Boolean = a == b

            override fun neqv(a: Any?, b: Any?): Boolean = a != b
        }
    }
}

inline fun <reified F> eq(): Eq<F> = instance(InstanceParametrizedType(Eq::class.java, listOf(typeLiteral<F>())))

inline fun <reified F> F.eqv(EQ: Eq<F> = eq(), b: F): Boolean = EQ.eqv(this, b)

inline fun <reified F> F.neqv(EQ: Eq<F> = eq(), b: F): Boolean = EQ.neqv(this, b)
