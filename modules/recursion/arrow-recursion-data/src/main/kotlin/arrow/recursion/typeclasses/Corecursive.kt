package arrow.recursion.typeclasses

import arrow.Kind
import arrow.core.Eval
import arrow.core.Tuple2
import arrow.core.identity
import arrow.recursion.*
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad

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
    fun a(s: A): Eval<T> = FF().run { coalg(s).map { Eval.later { it.fold(::identity) { a(it).value() } } }.embedT() }
    return a(this).value()
  }

  fun <A, M> A.gana(MM: Monad<M>, dist: DistFuncM<F, M, A>, alg: MAlgebra<F, M, A>): T {
    fun a(s: Kind<M, Kind<F, Kind<M, A>>>): Eval<T> = MM.run {
      FF().run {
        dist(s).map { a(MM.lift(alg).invoke(it.flatten())) }.embedT()
      }
    }
    return a(MM.just(alg(this))).value()
  }

  fun <A, M> A.gunfold(MM: Monad<M>, dist: DistFuncM<F, M, A>, alg: MAlgebra<F, M, A>): T =
    gana(MM, dist, alg)

  fun <A, B> A.coelgot(f: (Tuple2<A, Kind<F, B>>) -> B, coalg: Coalgebra<F, A>): B {
    fun h(a: A): Eval<B> = Eval.later {
      FF().run {
        f(
          Tuple2(a, coalg(a).map { h(it).value() })
        )
      }
    }
    return h(this).value()
  }
}
