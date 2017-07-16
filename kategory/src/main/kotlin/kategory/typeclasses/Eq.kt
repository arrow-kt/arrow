package kategory

interface Eq<in F> : Typeclass {
    fun eqv(a: F, b: F): Boolean

    fun neqv(a: F, b: F): Boolean =
            !eqv(a, b)

    companion object {
        inline fun any(): Eq<Any?> =
                EqAny

        object EqAny : Eq<Any?> {
            override fun eqv(a: Any?, b: Any?): Boolean =
                    a == b

            override fun neqv(a: Any?, b: Any?): Boolean =
                    a != b
        }
    }
}