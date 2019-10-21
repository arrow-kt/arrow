package arrow.recursion.typeclasses

/**
 * ank_macro_hierarchy(arrow.recursion.typeclasses.Birecursive)
 *
 * Typeclass for types that can be generically folded and unfolded with algebras and coalgebras.
 */
interface Birecursive<T, F> : Recursive<T, F>, Corecursive<T, F>
