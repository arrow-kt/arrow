package arrow.mtl

import arrow.Kind
import arrow.core.AndThen
import arrow.core.ForId
import arrow.core.Id
import arrow.core.Tuple2
import arrow.core.extensions.id.functor.functor
import arrow.core.extensions.id.monad.monad
import arrow.core.fix
import arrow.typeclasses.Monad
import arrow.typeclasses.Monoid

typealias Accum<S, A> = AccumT<S, ForId, A>

private fun <S, F, A> accum(MF: Monad<F>, f: (S) -> Tuple2<S, A>): AccumT<S, F, A> =
  AccumT {
    MF.just(f(it))
  }

fun <S, A> accum(f: (S) -> Tuple2<S, A>): Accum<S, A> =
  accum(Id.monad(), f)

fun <S, A> Accum<S, A>.runAccum(s: S): Tuple2<S, A> =
  runAccumT(s).fix().extract()

fun <S, A> Accum<S, A>.execAccum(s: S): S =
  execAccumT(Id.monad(), s).fix().extract()

fun <S, A> Accum<S, A>.evalAccum(s: S): A =
  evalAccumT(Id.monad(), s).fix().extract()

fun <S, A, B> Accum<S, A>.mapAccum(f: (Tuple2<S, A>) -> Tuple2<S, B>): Accum<S, B> =
  mapAccumT(AndThen(f).compose<Kind<ForId, Tuple2<S, A>>> { it.fix().extract() }.andThen(::Id))

fun <S, A, B> Accum<S, A>.map(fa: (A) -> B): Accum<S, B> =
  map(Id.functor(), fa)

fun <S, A, B> Accum<S, A>.flatMap(MS: Monoid<S>, fa: (A) -> Accum<S, B>): Accum<S, B> =
  flatMap(MS, Id.monad(), fa)
