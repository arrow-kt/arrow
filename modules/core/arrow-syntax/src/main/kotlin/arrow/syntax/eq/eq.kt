package arrow.syntax.eq

import arrow.typeclasses.Eq
import arrow.typeclasses.eq

/**
 * Syntax method for [Eq.eqv].
 *
 * @param F [Eq] instance for the type [F] you want to compare, by default the global instance will be used for type [F].
 * @param b the object to compare with.
 * @see [Eq.eqv]
 */
inline fun <reified F> F.eqv(EQ: Eq<F> = eq(), b: F): Boolean = EQ.eqv(this, b)

/**
 * Syntax method for [Eq.neqv].
 *
 * @param F [Eq] instance for the type [F] you want to compare, by default the global instance will be used for type [F].
 * @param b the object to compare with.
 * @see [Eq.neqv]
 */
inline fun <reified F> F.neqv(EQ: Eq<F> = eq(), b: F): Boolean = EQ.neqv(this, b)