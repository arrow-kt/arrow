package kategory

interface Eq<in F> : Typeclass {
    fun eqv(a: F, b: F): Boolean

    fun neqv(a: F, b: F): Boolean =
            !eqv(a, b)

    companion object {
        fun any(): Eq<Any?> = EqAny()

        private class EqAny : Eq<Any?> {
            override fun eqv(a: Any?, b: Any?): Boolean =
                    a == b

            override fun neqv(a: Any?, b: Any?): Boolean =
                    a != b
        }
    }
}