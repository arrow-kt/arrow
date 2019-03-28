package arrow.recursion

import arrow.Kind
import arrow.core.*
import arrow.core.extensions.eval.monad.binding
import arrow.free.Cofree
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

typealias MAlgebra<F, M, A> = (A) -> Kind<F, Kind<M, A>>

typealias WAlgebra<F, W, A> = (Kind<F, Kind<W, A>>) -> A

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

fun <F, B, A> distZygo(FF: Functor<F>, alg: Algebra<F, B>, fa: Kind<F, Tuple2<B, A>>): Tuple2<B, Kind<F, A>> = FF.run {
  Tuple2(alg(fa.map { it.a }), fa.map { it.b })
}

fun <F, A> distHisto(FF: Functor<F>, fa: Kind<F, Cofree<F, A>>): Cofree<F, Kind<F, A>> = FF.run {
  Cofree(FF, fa.map { it.extract() }, binding { fa.map { distHisto(FF, it.tail.value()) } })
}