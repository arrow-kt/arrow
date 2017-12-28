package arrow

/**
 * The combination of monad and comonad
 */
interface Bimonad<F> : Monad<F>, Comonad<F>, Typeclass

inline fun <reified F> bimonad(): Bimonad<F> = instance(InstanceParametrizedType(Bimonad::class.java, listOf(typeLiteral<F>())))
