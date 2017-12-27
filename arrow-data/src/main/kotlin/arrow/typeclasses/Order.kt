package arrow

/**
 * The [Order] type class is used to define a total ordering on some type [F] and is defined by being able to fully determine order between two instances.
 *
 * [Order] is a subtype of [Eq] and defines [eqv] in terms of [compare].
 *
 * @see [Eq]
 * @see <a href="http://arrow.io/docs/typeclasses/order/">Order documentation</a>
 */
interface Order<F> : Eq<F>, Typeclass {

    /**
     * Compare [a] with [b]. Returns an Int whose sign is:
     * - negative if `x < y`
     * - zero     if `x = y`
     * - positive if `x > y`
     *
     * @param a object to compare with [b]
     * @param b object to compare with [a]
     * @returns zero objects are equal, a negative number if [a] is less than [b], or a positive number if [a] greater than [b].
     */
    fun compare(a: F, b: F): Int

    /** @see [Eq.eqv] */
    override fun eqv(a: F, b: F): Boolean = compare(a, b) == 0

    /**
     * Check if [a] is `lower than` [b]
     *
     * @param a object to compare with [b]
     * @param b object to compare with [a]
     * @returns true if [a] is `lower than` [b] and false otherwise
     */
    fun lt(a: F, b: F): Boolean = compare(a, b) < 0

    /**
     * Check if [a] is `lower than or equal to` [b]
     *
     * @param a object to compare with [b]
     * @param b object to compare with [a]
     * @returns true if [a] is `lower than or equal to` [b] and false otherwise
     */
    fun lte(a: F, b: F): Boolean = compare(a, b) <= 0

    /**
     * Check if [a] is `greater than` [b]
     *
     * @param a object to compare with [b]
     * @param b object to compare with [a]
     * @returns true if [a] is `greater than` [b] and false otherwise
     */
    fun gt(a: F, b: F): Boolean = compare(a, b) > 0

    /**
     * Check if [a] is `greater than or equal to` [b]
     *
     * @param a object to compare with [b]
     * @param b object to compare with [a]
     * @returns true if [a] is `greater than or equal to` [b] and false otherwise
     */
    fun gte(a: F, b: F): Boolean = compare(a, b) >= 0

    /**
     * Determines the maximum of [a] and [b] in terms of order.
     *
     * @param a object to compare with [b]
     * @param b object to compare with [a]
     * @returns the maximum [a] if it is greater than [b] or [b] otherwise
     */
    fun max(a: F, b: F): F = if (gt(a, b)) a else b

    /**
     * Determines the minimum of [a] and [b] in terms of order.
     *
     * @param a object to compare with [b]
     * @param b object to compare with [a]
     * @returns the minimum [a] if it is less than [b] or [b] otherwise
     */
    fun min(a: F, b: F): F = if (lt(a, b)) a else b

    /**
     * Sorts [a] and [b] in terms of order.
     *
     * @param a object to compare with [b]
     * @param b object to compare with [a]
     * @returns a sorted [Tuple2] of [a] and [b].
     */
    fun sort(a: F, b: F): Tuple2<F, F> = if (gte(a, b)) a toT b else b toT a

    companion object {

        /**
         * Construct an [Order] from a function `(F, F) -> Int`.
         *
         * @param compare a function that defines the order for 2 objects of type [F].
         * @returns an [Order] instance that is defined by the [compare] function.
         */
        inline operator fun <F> invoke(crossinline compare: (F, F) -> Int): Order<F> = object : Order<F> {
            override fun compare(a: F, b: F): Int = compare(a, b)
        }

        /**
         * Construct an [Order] that defines all instances as equal for type [F].
         *
         * @returns an [Order] instance wherefore all instances of type [F] are equal.
         */
        fun <F> allEqual(): Order<F> = object : Order<F> {
            override fun compare(a: F, b: F): Int = 0
        }

    }

}

/**
 * Method to lookup instance of [Order] for a type [F].
 *
 * @return [Order] instance for type [F].
 */
inline fun <reified F> order(): Order<F> = instance(InstanceParametrizedType(Order::class.java, listOf(typeLiteral<F>())))

/**
 * Syntax method for [Order.compare].
 *
 * @param O [Order] for the type [F] you want to compare by default the global instance will be used for type [F].
 * @param b the object to compare with.
 * @see [Order.compare]
 */
inline fun <reified F> F.compare(O: Order<F> = order(), b: F): Boolean = O.lt(this, b)

/**
 * Syntax method for [Order.lt].
 *
 * @param O [Order] for the type [F] you want to compare by default the global instance will be used for type [F].
 * @param b the object to compare with.
 * @see [Order.lt]
 */
inline fun <reified F> F.lt(O: Order<F> = order(), b: F): Boolean = O.lt(this, b)

/**
 * Syntax method for [Order.lte].
 *
 * @param O [Order] instance for the type [F] you want to compare, by default the global instance will be used for type [F].
 * @param b the object to compare with.
 * @see [Order.lte]
 */
inline fun <reified F> F.lte(O: Order<F> = order(), b: F): Boolean = O.lte(this, b)

/**
 * Syntax method for [Order.gt].
 *
 * @param O [Order] for the type [F] you want to compare by default the global instance will be used for type [F].
 * @param b the object to compare with.
 * @see [Order.gt]
 */
inline fun <reified F> F.gt(O: Order<F> = order(), b: F): Boolean = O.gt(this, b)

/**
 * Syntax method for [Order.gte].
 *
 * @param O [Order] for the type [F] you want to compare by default the global instance will be used for type [F].
 * @param b the object to compare with.
 * @see [Order.gt]
 */
inline fun <reified F> F.gte(O: Order<F> = order(), b: F): Boolean = O.gte(this, b)

/**
 * Get an [Order] instance for a type that implements [Comparable].
 *
 * @param F which is constraint in [Comparable] for [F].
 */
fun <F : Comparable<F>> toOrder(): Order<F> = object : Order<F> {
    override fun compare(a: F, b: F): Int = a.compareTo(b)
}

/**
 * Returns a list of all elements sorted according to the specified [order].
 *
 * @param O [Order] for the type [F] you want to compare by default the global instance will be used for type [F].
 * @returns sorted list by [O].
 */
inline fun <reified F> Iterable<F>.sorted(O: Order<F> = order()): List<F> = sortedWith(Comparator { o1, o2 ->
    O.compare(o1, o2)
})