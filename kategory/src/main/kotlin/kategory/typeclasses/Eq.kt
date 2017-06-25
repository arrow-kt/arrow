package kategory

interface Eq<in F> : Typeclass {
    fun eqv(a: F, b: F): Boolean

    fun neqv(a: F, b: F): Boolean =
            !eqv(a, b)

    companion object {
        operator fun <F> invoke() = object : Eq<F> {
            override fun eqv(a: F, b: F): Boolean =
                    a == b

            override fun neqv(a: F, b: F): Boolean =
                    a != b
        }
    }
}