package arrow.recursion.typeclasses

import arrow.Kind
import arrow.core.Eval
import arrow.typeclasses.Functor
import arrow.recursion.Algebra
import arrow.recursion.Coalgebra
import arrow.recursion.hylo

/**
 * ank_macro_hierarchy(arrow.recursion.typeclasses.Recursive)
 *
 * Typeclass for types that can be generically folded with algebras.
 */
interface Recursive<T> {
  /**
   * Implementation for project.
   */
  fun <F> Functor<F>.projectT(tf: Kind<T, F>): Kind<F, Kind<T, F>>

  /**
   * Creates a coalgebra given a functor.
   */
  fun <F> Functor<F>.project(): Coalgebra<F, Kind<T, F>> = { projectT(it) }

  /**
   * Fold generalized over any recursive type.
   */
  fun <F, A> Functor<F>.cata(tf: Kind<T, F>, alg: Algebra<F, Eval<A>>): A =
    hylo(alg, project(), tf)
}
