package arrow.recursion

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.FunctionK
import arrow.core.Tuple2
import arrow.free.Cofree
import arrow.free.Free
import arrow.recursion.data.Fix
import arrow.recursion.data.FreeR
import arrow.recursion.typeclasses.Corecursive
import arrow.recursion.typeclasses.Recursive
import arrow.typeclasses.Functor

fun <F, A> Algebra(it: Algebra<F, A>) = it

fun <F, A> Coalgebra(it: Coalgebra<F, A>) = it

/**
 * Fold over a kind.
 */
typealias Algebra<F, A> = (Kind<F, A>) -> A

/**
 * Unfold over a kind.
 */
typealias Coalgebra<F, A> = (A) -> Kind<F, A>

typealias RAlgebra<F, T, A> = (Kind<F, Tuple2<T, A>>) -> A

typealias RCoalgebra<F, T, A> = (A) -> Kind<F, Either<T, A>>

typealias CVAlgebra<F, A> = (Kind<F, Cofree<F, A>>) -> A

typealias CVCoalgebra<F, A> = (A) -> Kind<F, FreeR<F, A>>

/**
 * The composition of cata and ana.
 */
fun <F, A, B> A.hylo(
  alg: Algebra<F, Eval<B>>,
  coalg: Coalgebra<F, A>,
  FF: Functor<F>
): B {
  fun h(a: A): Eval<B> = FF.run { alg(coalg(a).map { Eval.defer { h(it) } }) }
  return h(this).value()
}

fun <S, F, T, G> S.hoist(SR: Recursive<S, F>, SC: Corecursive<T, G>, f: FunctionK<F, G>): T = SR.run {
  cata { SC.run { f(it).embedT() } }
}