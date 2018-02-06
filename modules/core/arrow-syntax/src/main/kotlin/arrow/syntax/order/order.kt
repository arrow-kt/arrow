package arrow.syntax.order

import arrow.typeclasses.Order
import arrow.typeclasses.order

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