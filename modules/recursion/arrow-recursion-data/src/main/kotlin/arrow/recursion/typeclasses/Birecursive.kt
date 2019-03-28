package arrow.recursion.typeclasses

import arrow.Kind
import arrow.core.Eval
import arrow.core.FunctionK
import arrow.core.extensions.eval.monad.binding
import arrow.recursion.*
import arrow.typeclasses.Comonad
import arrow.typeclasses.Monad

/**
 * ank_macro_hierarchy(arrow.recursion.typeclasses.Birecursive)
 *
 * Typeclass for types that can be generically folded and unfolded with algebras and coalgebras.
 */
interface Birecursive<T, F> : Recursive<T, F>, Corecursive<T, F> {

  fun T.lambek() = cata<Kind<F, T>> { fa -> Eval.later { FF().run { fa.map { it.value().map { Eval.now(it) }.embedT().value() } } } }

  fun Kind<F, Eval<T>>.colambek() = ana { fa -> FF().run { fa.map { it.value().projectT().map { Eval.now(it) } } } }

  fun <A> T.prepro(trans: FunctionK<F, F>, alg: Algebra<F, Eval<A>>): A {
    fun c(x: T): Eval<A> = FF().run {
      alg(x.projectT().map { c(it.hoist(this@Birecursive, this@Birecursive, trans)) })
    }
    return c(this).value()
  }

  fun <A, W> T.gprepro(CW: Comonad<W>, dist: DistFunc<F, W, A>, trans: FunctionK<F, F>, alg: WAlgebra<F, W, A>): A {
    fun c(x: T): Eval<Kind<W, A>> = FF().run {
      Eval.later {
        CW.run {
          dist(
            x.projectT().map { c(it.hoist(this@Birecursive, this@Birecursive, trans)).value().duplicate() }
          ).map(alg)
        }
      }
    }
    return CW.run { c(this@gprepro).value().extract() }
  }

  fun <A> A.postPro(trans: FunctionK<F, F>, coalg: Coalgebra<F, A>): T {
    fun a(x: A): Eval<T> = FF().run {
      coalg(x).map { binding { a(it).bind().hoist(this@Birecursive, this@Birecursive, trans) } }.embedT()
    }
    return a(this).value()
  }

  fun <A, M> A.gpostpro(MM: Monad<M>, dist: DistFuncM<F, M, A>, trans: FunctionK<F, F>, alg: MAlgebra<F, M, A>): T {
    fun c(a: Kind<M, A>): Eval<T> = MM.run {
      FF().run {
        dist(
          MM.lift(alg).invoke(a)
        ).map { Eval.later { c(it.flatten()).value().hoist(this@Birecursive, this@Birecursive, trans) } }
          .embedT()
      }
    }
    return c(MM.just(this)).value()
  }
}
