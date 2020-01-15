package arrow.mtl.extensions

import arrow.Kind
import arrow.core.Either
import arrow.extension
import arrow.mtl.AccumT
import arrow.mtl.AccumTPartialOf
import arrow.mtl.fix
import arrow.typeclasses.Applicative
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
import arrow.typeclasses.Monoid

@extension
interface AccumTFunctor<W, M> : Functor<AccumTPartialOf<W, M>> {

  fun MF(): Functor<M>

  override fun <A, B> Kind<AccumTPartialOf<W, M>, A>.map(f: (A) -> B): Kind<AccumTPartialOf<W, M>, B> =
    this.fix().map(MF(), f)
}

@extension
interface AccumTApplicative<W, M> : Applicative<AccumTPartialOf<W, M>> {
  fun MW(): Monoid<W>
  fun MF(): Monad<M>

  override fun <A> just(a: A): Kind<AccumTPartialOf<W, M>, A> =
    AccumT.just(MW(), MF(), a)

  override fun <A, B> Kind<AccumTPartialOf<W, M>, A>.ap(ff: Kind<AccumTPartialOf<W, M>, (A) -> B>): Kind<AccumTPartialOf<W, M>, B> =
    fix().ap(MW(), MF(), ff)
}

@extension
interface AccumTMonad<W, M> : Monad<AccumTPartialOf<W, M>> {

  fun MW(): Monoid<W>
  fun MF(): Monad<M>

  override fun <A> just(a: A): Kind<AccumTPartialOf<W, M>, A> =
    AccumT.just(MW(), MF(), a)

  override fun <A, B> Kind<AccumTPartialOf<W, M>, A>.flatMap(f: (A) -> Kind<AccumTPartialOf<W, M>, B>): Kind<AccumTPartialOf<W, M>, B> =
    this.fix().flatMap(MW(), MF(), f)

  override fun <A, B> tailRecM(a: A, f: (A) -> Kind<AccumTPartialOf<W, M>, Either<A, B>>): Kind<AccumTPartialOf<W, M>, B> =
    AccumT.tailRecM(MF(), a, f)

  override fun <A, B> Kind<AccumTPartialOf<W, M>, A>.ap(ff: Kind<AccumTPartialOf<W, M>, (A) -> B>): Kind<AccumTPartialOf<W, M>, B> =
    fix().ap(MW(), MF(), ff)
}
