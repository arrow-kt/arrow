package arrow.recursion.typeclasses

import arrow.core.Eval
import arrow.core.FunctionK
import arrow.recursion.Algebra
import arrow.recursion.Coalgebra
import arrow.recursion.hoist

/**
 * ank_macro_hierarchy(arrow.recursion.typeclasses.Birecursive)
 *
 * Typeclass for types that can be generically folded and unfolded with algebras and coalgebras.
 */
interface Birecursive<T, F> : Recursive<T, F>, Corecursive<T, F> {

  fun <A> T.prepro(trans: FunctionK<F, F>, alg: Algebra<F, Eval<A>>): A {
    fun c(x: T): Eval<A> = FF().run {
      alg(x.projectT().map { Eval.defer { c(it.hoist(this@Birecursive, this@Birecursive, trans)) } })
    }
    return c(this).value()
  }

  fun <A> A.postPro(trans: FunctionK<F, F>, coalg: Coalgebra<F, A>): T {
    fun a(x: A): Eval<T> = FF().run {
      coalg(x).map { Eval.defer { a(it) }.map { it.hoist(this@Birecursive, this@Birecursive, trans) } }.embedT()
    }
    return a(this).value()
  }

}
