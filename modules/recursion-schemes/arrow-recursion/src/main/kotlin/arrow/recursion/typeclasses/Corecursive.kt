package arrow.recursion.typeclasses

import arrow.Kind
import arrow.core.Eval
import arrow.typeclasses.Functor
import arrow.recursion.Algebra
import arrow.recursion.Coalgebra
import arrow.recursion.hylo

/**
 * Typeclass for types that can be generically unfolded with coalgebras.
 */
interface Corecursive<T> {
  /**
   * Implementation for embed.
   */
  fun <F> embedT(FF: Functor<F>, t: Kind<F, Eval<Kind<T, F>>>): Eval<Kind<T, F>>

  /**
   * Creates a algebra given a functor.
   */
  fun <F> embed(FF: Functor<F>): Algebra<F, Eval<Kind<T, F>>> = { embedT(FF, it) }

  /**
   * Unfold into any recursive type.
   */
  fun <F, A> A.ana(FF: Functor<F>, coalg: Coalgebra<F, A>): Kind<T, F> =
    hylo(FF, embed(FF), coalg, this)
}
