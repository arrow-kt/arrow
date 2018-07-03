package arrow.recursion.typeclasses

/**
 * Typeclass for types that can be generically folded and unfolded with algebras and coalgebras.
 */
interface Birecursive<F> : Recursive<F>, Corecursive<F>
