package arrow.free.extensions

import arrow.Kind
import arrow.core.FunctionK

import arrow.free.FreeApplicative
import arrow.free.FreeApplicativePartialOf
import arrow.free.fix
import arrow.extension
import arrow.typeclasses.Applicative
import arrow.typeclasses.Eq
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
import arrow.undocumented

@extension
@undocumented
interface FreeApplicativeFunctor<S> : Functor<FreeApplicativePartialOf<S>> {
  override fun <A, B> Kind<FreeApplicativePartialOf<S>, A>.map(f: (A) -> B): FreeApplicative<S, B> = fix().map(f)
}

@extension
@undocumented
interface FreeApplicativeApplicative<S> : Applicative<FreeApplicativePartialOf<S>>, FreeApplicativeFunctor<S> {
  override fun <A> just(a: A): FreeApplicative<S, A> = FreeApplicative.just(a)

  override fun <A, B> Kind<FreeApplicativePartialOf<S>, A>.ap(ff: Kind<FreeApplicativePartialOf<S>, (A) -> B>): FreeApplicative<S, B> =
    fix().ap(ff.fix())

  override fun <A, B> Kind<FreeApplicativePartialOf<S>, A>.map(f: (A) -> B): FreeApplicative<S, B> = fix().map(f)
}

@extension
@undocumented
interface FreeApplicativeEq<F, G, A> : Eq<Kind<FreeApplicativePartialOf<F>, A>> {
  fun MG(): Monad<G>

  fun FK(): FunctionK<F, G>

  override fun Kind<FreeApplicativePartialOf<F>, A>.eqv(b: Kind<FreeApplicativePartialOf<F>, A>): Boolean =
    fix().foldMap(FK(), MG()) == b.fix().foldMap(FK(), MG())
}
