package arrow.recursion.typeclasses

import arrow.Kind
import arrow.core.Eval
import arrow.core.Tuple2
import arrow.recursion.Algebra
import arrow.recursion.Coalgebra
import arrow.recursion.RCoalgebra
import arrow.recursion.hylo
import arrow.typeclasses.Functor

/**
 * ank_macro_hierarchy(arrow.recursion.typeclasses.Corecursive)
 *
 * Typeclass for types that can be generically unfolded with coalgebras.
 */
interface Corecursive<T, F> {

  fun FF(): Functor<F>

  /**
   * Implementation for embed.
   */
  fun Kind<F, Eval<T>>.embedT(): Eval<T>

  /**
   * Creates a algebra given a functor.
   */
  fun embed(): Algebra<F, Eval<T>> = { it.embedT() }

  /**
   * Unfold into any recursive type.
   */
  fun <A> A.ana(coalg: Coalgebra<F, A>): T = hylo(embed(), coalg, FF())

  fun <A> A.apo(coalg: RCoalgebra<F, T, A>): T {
    fun a(s: Eval<A>): Eval<T> = FF().run {
      s.flatMap { coalg(it).map { it.fold({ Eval.now(it) }, { a(Eval.now(it)) }) }.embedT() }
    }
    return a(Eval.now(this)).value()
  }

  fun <A, B> A.coelgot(f: (Tuple2<A, Eval<Kind<F, Eval<B>>>>) -> Eval<B>, coalg: Coalgebra<F, A>): B {
    fun h(a: A): Eval<B> =
      FF().run {
        f(
          Tuple2(a, Eval.later { coalg(a).map { Eval.defer { h(it) } } })
        )
      }
    return h(this).value()
  }
}
