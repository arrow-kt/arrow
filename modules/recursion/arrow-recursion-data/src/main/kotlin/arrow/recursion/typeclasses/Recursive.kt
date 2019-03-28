package arrow.recursion.typeclasses

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.Tuple2
import arrow.core.identity
import arrow.free.Cofree
import arrow.free.Trampoline
import arrow.recursion.*
import arrow.typeclasses.Comonad
import arrow.typeclasses.Functor

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
            Tuple2(it, p(it))
          }
        )
      }
    return p(this).value()
  }

  // TODO gpara

  fun <A, W> T.gcata(CW: Comonad<W>, dist: DistFunc<F, W, A>, alg: WAlgebra<F, W, A>): A {
    fun c(x: T): Eval<Kind<W, Kind<F, Kind<W, A>>>> = FF().run {
      dist(
        x.projectT().map {
          CW.run { c(it).map { it.map(alg).duplicate() } }
        }
      )
    }
    return CW.run { alg(c(this@gcata).value().extract()) }
  }

  fun <A, W> T.gfold(CW: Comonad<W>, dist: DistFunc<F, W, A>, alg: WAlgebra<F, W, A>): A =
    gcata(CW, dist, alg)

  /*
  fun <A, B> T.zygo(alg: Algebra<F, B>, f: (Kind<F, Tuple2<B, A>>) -> A): A =
    gfold(Tuple2.comonad<B>(), {
      distZygo(FF(), alg, FF().run { it.map { it.fix().map { it.fix() } } })
    }, {
      f(FF().run { it.map { it.fix() } })
    })
*/

  fun <A> T.histo(alg: (Kind<F, Cofree<F, A>>) -> A): A {
    fun go(t: T): Cofree<F, A> = FF().run {
      t.projectT().map { go(it) }.let {
        Cofree(FF(), alg(it), Eval.now(it))
      }
    }
    return go(this).extract()
  }
  /*
  gcata<A, CofreePartialOf<F>>(Cofree.comonad(), {
    distHisto(FF(), FF().run { it.map { it.fix().map { it.fix() } } })
  }, {
    alg(FF().run { it.map { it.fix() } })
  })
  */

  fun <A, B> B.elgot(alg: Algebra<F, A>, f: (B) -> Either<A, Kind<F, B>>): A {
    fun h(b: B): Eval<A> = Eval.later {
      f(b).fold(::identity) { FF().run { alg(it.map { h(it).value() }) } }
    }
    return h(this).value()
  }

}
