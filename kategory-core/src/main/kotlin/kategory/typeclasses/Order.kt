package kategory

interface Order<F> : Eq<F>, Typeclass {

    /**
     * Compare [a] with [b]. Returns an Int whose sign is:
     * - negative if `x < y`
     * - zero     if `x = y`
     * - positive if `x > y`
     */
    fun compare(a: F, b: F): Int

    override fun eqv(a: F, b: F): Boolean = compare(a, b) == 0

    fun lt(a: F, b: F): Boolean = compare(a, b) < 0
    fun lte(a: F, b: F): Boolean = compare(a, b) <= 0
    fun gt(a: F, b: F): Boolean = compare(a, b) > 0
    fun gte(a: F, b: F): Boolean = compare(a, b) >= 0

    fun max(a: F, b: F): F = if (gt(a, b)) a else b
    fun min(a: F, b: F): F = if (lt(a, b)) a else b
    fun sort(a: F, b: F): Tuple2<F, F> = if (gte(a, b)) a toT b else b toT a

    companion object {

        inline operator fun <F> invoke(crossinline compare: (F, F) -> Int): Order<F> = object : Order<F> {
            override fun compare(a: F, b: F): Int = compare(a, b)
        }

        fun <F> allEqual(): Order<F> = object : Order<F> {
            override fun compare(a: F, b: F): Int = 0
        }

    }

}

inline fun <reified F> order(): Order<F> = instance(InstanceParametrizedType(Order::class.java, listOf(typeLiteral<F>())))

inline fun <reified F> F.lt(O: Order<F> = order(), b: F): Boolean = O.lt(this, b)

inline fun <reified F> F.lte(O: Order<F> = order(), b: F): Boolean = O.lte(this, b)

inline fun <reified F> F.gt(O: Order<F> = order(), b: F): Boolean = O.gt(this, b)

inline fun <reified F> F.gte(O: Order<F> = order(), b: F): Boolean = O.gte(this, b)

fun <F: Comparable<F>> toOrder(): Order<F> = object : Order<F> {
    override fun compare(a: F, b: F): Int = a.compareTo(b)
}
