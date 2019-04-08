package arrow.recursion.typeclasses

import arrow.core.FunctionK
import arrow.core.andThen
import arrow.core.compose
import arrow.recursion.Algebra
import arrow.recursion.Coalgebra
import arrow.recursion.hylo

/**
 * ank_macro_hierarchy(arrow.recursion.typeclasses.Birecursive)
 *
 * Typeclass for types that can be generically folded and unfolded with algebras and coalgebras.
 */
interface Birecursive<T, F> : Recursive<T, F>, Corecursive<T, F>
