package kategory

interface Eq<in F> : Typeclass {
    fun eqv(a: F, b: F): Boolean

    fun neqv(a: F, b: F): Boolean =
            !eqv(a, b)

    companion object {
        fun any(): Eq<Any?> =
                EqAny

        object EqAny : Eq<Any?> {
            override fun eqv(a: Any?, b: Any?): Boolean =
                    a == b

            override fun neqv(a: Any?, b: Any?): Boolean =
                    a != b
        }
    }
}

inline fun <reified F> eq(): Eq<F> =
        instance(InstanceParametrizedType(Eq::class.java, listOf(F::class.java)))

inline fun <reified F> F.eqv(FT: Eq<F> = eq(), b: F) : Boolean =
        FT.eqv(this, b)

inline fun <reified F> F.neqv(FT: Eq<F> = eq(), b: F) : Boolean =
        FT.neqv(this, b)