package arrow.recursion.typeclasses

import arrow.Kind
import arrow.core.Eval
import arrow.core.Tuple2
import arrow.core.fix
import arrow.recursion.*
import arrow.recursion.data.fix
import arrow.recursion.pattern.FreeF
import arrow.recursion.pattern.FreeR
import arrow.recursion.pattern.fix
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
    fun go(v: Eval<A>): Eval<T> = FF().run {
      v.flatMap { coalg(it).map { it.fold({ Eval.now(it) }, { go(Eval.now(it)) }) }.embedT() }
    }
    return go(Eval.now(this)).value()
  }

  fun <A> A.futu(coalg: CVCoalgebra<F, A>): T =
    futuFGo(this@Corecursive, this, coalg).extract()

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

// Futu helpers
private fun <F, T, A> futuFGo(CR: Corecursive<T, F>, a: A, coalg: CVCoalgebra<F, A>): Eval<T> = CR.FF().run {
  CR.run { coalg(a).map { futuFWorker(CR, Eval.now(it), coalg) }.embedT() }
}

private fun <F, T, A> futuFWorker(CR: Corecursive<T, F>, f: Eval<FreeR<F, A>>, coalg: CVCoalgebra<F, A>): Eval<T> = f.flatMap { f ->
  when (val f = f.unfix.fix()) {
    is FreeF.Pure -> futuFGo(CR, f.e, coalg)
    is FreeF.Impure -> CR.run { CR.FF().run { f.fa.map { futuFWorker(CR, it.fix().map { it.fix() }, coalg) }.embedT() } }
  }
}