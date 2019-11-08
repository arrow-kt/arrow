package arrow.core

/**
 * The monoid of endomorphisms under composition.
 */
data class Endo<A>(val f: (A) -> A)
