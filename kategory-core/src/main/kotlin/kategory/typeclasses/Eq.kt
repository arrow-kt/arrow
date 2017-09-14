package kategory

interface Eq<in F> : Typeclass {
    fun eqv(a: F, b: F): Boolean

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
