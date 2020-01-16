package arrow.mtl.extensions

import arrow.Kind
import arrow.Kind2
import arrow.core.Either
import arrow.core.toT
import arrow.extension
import arrow.mtl.AccumT
import arrow.mtl.AccumTPartialOf
import arrow.mtl.ForAccumT
import arrow.mtl.fix
import arrow.mtl.typeclasses.MonadTrans
import arrow.typeclasses.Applicative
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
import arrow.typeclasses.Monoid

@extension
interface AccumTFunctor<S, F> : Functor<AccumTPartialOf<S, F>> {

  fun MF(): Functor<F>

  override fun <A, B> Kind<AccumTPartialOf<S, F>, A>.map(f: (A) -> B): Kind<AccumTPartialOf<S, F>, B> =
    this.fix().map(MF(), f)
}

@extension
interface AccumTApplicative<S, F> : Applicative<AccumTPartialOf<S, F>> {
  fun MW(): Monoid<S>
  fun MF(): Monad<F>

  override fun <A> just(a: A): Kind<AccumTPartialOf<S, F>, A> =
    AccumT.just(MW(), MF(), a)

  override fun <A, B> Kind<AccumTPartialOf<S, F>, A>.ap(ff: Kind<AccumTPartialOf<S, F>, (A) -> B>): Kind<AccumTPartialOf<S, F>, B> =
    fix().ap(MW(), MF(), ff)
}

@extension
interface AccumTMonad<S, F> : Monad<AccumTPartialOf<S, F>> {

  fun MS(): Monoid<S>
  fun MF(): Monad<F>

  override fun <A> just(a: A): Kind<AccumTPartialOf<S, F>, A> =
    AccumT.just(MS(), MF(), a)

  override fun <A, B> Kind<AccumTPartialOf<S, F>, A>.flatMap(f: (A) -> Kind<AccumTPartialOf<S, F>, B>): Kind<AccumTPartialOf<S, F>, B> =
    this.fix().flatMap(MS(), MF(), f)

  override fun <A, B> tailRecM(a: A, f: (A) -> Kind<AccumTPartialOf<S, F>, Either<A, B>>): Kind<AccumTPartialOf<S, F>, B> =
    AccumT.tailRecM(MF(), a, f)

  override fun <A, B> Kind<AccumTPartialOf<S, F>, A>.ap(ff: Kind<AccumTPartialOf<S, F>, (A) -> B>): Kind<AccumTPartialOf<S, F>, B> =
    fix().ap(MS(), MF(), ff)
}

@extension
interface AccumtTMonadTrans<S> : MonadTrans<Kind<ForAccumT, S>> {

  fun MS(): Monoid<S>

  override fun <G, A> Kind<G, A>.liftT(MF: Monad<G>): Kind2<Kind<ForAccumT, S>, G, A> {

    val accumTFun = { _: S ->
      MF.run {
        flatMap { a ->
          MF.just(a toT MS().empty())
        }
      }
    }

    return AccumT(MF.just(accumTFun))
  }
}
