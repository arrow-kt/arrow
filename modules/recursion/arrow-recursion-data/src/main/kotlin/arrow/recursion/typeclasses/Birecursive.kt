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
interface Birecursive<T, F> : Recursive<T, F>, Corecursive<T, F> {

  fun <A> T.prepro(trans: FunctionK<F, F>, alg: Algebra<F, A>): A =
    hylo(alg compose trans::invoke, project(), FF())

  fun <A> A.postPro(trans: FunctionK<F, F>, coalg: Coalgebra<F, A>): T =
    hylo(embed(), coalg andThen trans::invoke, FF())
}
