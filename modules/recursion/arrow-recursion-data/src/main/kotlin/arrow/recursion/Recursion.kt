package arrow.recursion

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.Tuple2
import arrow.free.Cofree
import arrow.recursion.data.fix
import arrow.recursion.pattern.FreeF
import arrow.recursion.pattern.FreeR
import arrow.recursion.pattern.fix
import arrow.typeclasses.*

typealias Algebra<F, A> = (Kind<F, A>) -> A
typealias AlgebraM<F, M, A> = (Kind<F, A>) -> Kind<M, A>

typealias Coalgebra<F, A> = (A) -> Kind<F, A>
typealias CoalgebraM<F, M, A> = (A) -> Kind<M, Kind<F, A>>

typealias RAlgebra<F, T, A> = (Kind<F, Tuple2<T, A>>) -> A
typealias RAlgebraM<F, M, T, A> = (Kind<F, Tuple2<T, A>>) -> Kind<M, A>

typealias RCoalgebra<F, T, A> = (A) -> Kind<F, Either<T, A>>
typealias RCoalgebraM<F, M, T, A> = (A) -> Kind<M, Kind<F, Either<T, A>>>

typealias CVAlgebra<F, A> = (Kind<F, Cofree<F, A>>) -> A
typealias CVAlgebraM<F, M, A> = (Kind<F, Cofree<F, A>>) -> Kind<M, A>

typealias CVCoalgebra<F, A> = (A) -> Kind<F, FreeR<F, A>>
typealias CVCoalgebraM<F, M, A> = (A) -> Kind<M, Kind<F, FreeR<F, A>>>

fun <F, A, B> A.hylo(
  alg: Algebra<F, B>,
  coalg: Coalgebra<F, A>,
  FF: Functor<F>
): B {
  fun h(a: A): B = FF.run { alg(coalg(a).map { h(it) }) }
  return h(this)
}

fun <F, W, A, B> A.hyloC(
  alg: (Kind<F, Kind<W, B>>) -> B,
  coalg: (A) -> Kind<F, Kind<W, A>>,
  FF: Functor<F>,
  WF: Functor<W>
): B = hylo({
  alg(it.unnest())
}, {
  coalg(it).nest()
}, FF.compose(WF))

fun <F, M, A, B> A.hyloM(
  alg: AlgebraM<F, M, B>,
  coalg: CoalgebraM<F, M, A>,
  TF: Traverse<F>,
  MM: Monad<M>
): Kind<M, B> = hyloC({
  MM.run {
    it.flatMap {
      TF.run {
        it.sequence(MM).flatMap(alg)
      }
    }
  }
}, coalg, MM, TF)

fun <F, W, M, A, B> A.hyloMC(
  alg: (Kind<F, Kind<W, B>>) -> Kind<M, B>,
  coalg: (A) -> Kind<M, Kind<F, Kind<W, A>>>,
  TF: Traverse<F>,
  TW: Traverse<W>,
  AW: Applicative<W>,
  MM: Monad<M>
): Kind<M, B> = hyloM({
  alg(it.unnest())
}, {
  MM.run { coalg(it).map { it.nest() } }
}, TF.compose(TW, AW), MM)

fun <F, A, B> A.chrono(
  alg: CVAlgebra<F, B>,
  coalg: CVCoalgebra<F, A>,
  FF: Functor<F>
): B =
  FreeF.pure<F, A>(this)
    .hylo<F, FreeR<F, A>, Cofree<F, B>>({
      Cofree(FF, alg(it), Eval.now(it))
    }, {
      when (val fa = it.unfix.fix()) {
        is FreeF.Pure -> coalg(fa.e)
        is FreeF.Impure -> FF.run { fa.fa.map { it.value().fix() } }
      }
    }, FF).head

fun <F, M, A, B> A.chronoM(
  alg: CVAlgebraM<F, M, B>,
  coalg: CVCoalgebraM<F, M, A>,
  TF: Traverse<F>,
  MM: Monad<M>
): Kind<M, B> =
  MM.run {
    FreeF.pure<F, A>(this@chronoM)
      .hyloM<F, M, FreeR<F, A>, Cofree<F, B>>(
        {
          alg(it).map { res -> Cofree(TF, res, Eval.now(it)) }
        },
        {
          when (val fa = it.unfix.fix()) {
            is FreeF.Pure -> coalg(fa.e)
            is FreeF.Impure -> just(TF.run { fa.fa.map { it.value().fix() } })
          }
        },
        TF, MM
      ).map { it.head }
  }

fun <F, A, B> A.dyna(
  alg: CVAlgebra<F, B>,
  coalg: Coalgebra<F, A>,
  FF: Functor<F>
): B =
  hylo<F, A, Cofree<F, B>>({
    Cofree(FF, alg(it), Eval.now(it))
  }, coalg, FF).head

fun <F, M, A, B> A.dynaM(
  alg: CVAlgebraM<F, M, B>,
  coalg: CoalgebraM<F, M, A>,
  TF: Traverse<F>,
  MM: Monad<M>
): Kind<M, B> =
  MM.run {
    hyloM<F, M, A, Cofree<F, B>>({
      alg(it).map { res -> Cofree(TF, res, Eval.now(it)) }
    }, coalg, TF, MM).map { it.head }
  }