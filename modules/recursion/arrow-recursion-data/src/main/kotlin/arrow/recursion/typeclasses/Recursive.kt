package arrow.recursion.typeclasses

import arrow.Kind
import arrow.core.*
import arrow.core.extensions.eval.applicative.applicative
import arrow.free.Cofree
import arrow.recursion.Algebra
import arrow.recursion.Coalgebra
import arrow.recursion.RAlgebra
import arrow.recursion.hylo
import arrow.typeclasses.Functor
import arrow.typeclasses.Traverse

/**
 * ank_macro_hierarchy(arrow.recursion.typeclasses.Recursive)
 *
 * Typeclass for types that can be generically folded with algebras.
 */
interface Recursive<T, F> {

  fun FF(): Functor<F>

  /**
   * Implementation for project.
   */
  fun T.projectT(): Kind<F, T>

  /**
   * Creates a coalgebra given a functor.
   */
  fun project(): Coalgebra<F, T> = { it.projectT() }

  /**
   * Fold generalized over any recursive type.
   */
  fun <A> T.cata(alg: Algebra<F, Eval<A>>): A = hylo(alg, project(), FF())

  fun <A> T.para(alg: RAlgebra<F, T, Eval<A>>): A {
    fun p(x: T): Eval<A> =
      FF().run {
        alg(
          x.projectT().map {
            Tuple2(it, Eval.defer { p(it) })
          }
        )
      }
    return p(this).value()
  }

  fun <A> T.histo(alg: (Kind<F, Cofree<F, A>>) -> A): A {
    fun go(t: T): Cofree<F, A> = FF().run {
      t.projectT().map { go(it) }.let {
        Cofree(FF(), alg(it), Eval.now(it))
      }
    }
    return go(this).extract()
  }

  fun <A> T.histoStackSafe(TF: Traverse<F>, alg: (Kind<F, Cofree<F, A>>) -> A): A {
    fun go(t: T): Eval<Cofree<F, A>> = TF.run {
      t.projectT().traverse(Eval.applicative()) { Eval.defer { go(it) } }.fix().map {
        Cofree(FF(), alg(it), Eval.now(it))
      }
    }
    return go(this).extract().extract()
  }

  fun <A, B> B.elgot(alg: Algebra<F, Eval<A>>, f: (B) -> Either<A, Kind<F, Eval<B>>>): A {
    fun h(b: Eval<B>): Eval<A> =
      b.flatMap { f(it).fold({ Eval.now(it) }) { FF().run { alg(it.map { h(it) }) } } }

    return h(Eval.now(this)).value()
  }
}
