package arrow.streams.internal

import arrow.*
import arrow.core.*
import arrow.effects.typeclasses.*
import arrow.typeclasses.*
import arrow.streams.internal.ap as apply
import arrow.streams.internal.handleErrorWith as handleErrorW
import arrow.streams.internal.bracketCase as bracketC

@extension
interface FreeCFunctor<F> : Functor<FreeCPartialOf<F>> {
  override fun <A, B> Kind<FreeCPartialOf<F>, A>.map(f: (A) -> B): Kind<FreeCPartialOf<F>, B> =
    this.fix().map(f)
}

@extension
interface FreeCApplicative<F> : Applicative<FreeCPartialOf<F>> {
  override fun <A> just(a: A): Kind<FreeCPartialOf<F>, A> =
    FreeC.just(a)

  override fun <A, B> Kind<FreeCPartialOf<F>, A>.ap(ff: Kind<FreeCPartialOf<F>, (A) -> B>): Kind<FreeCPartialOf<F>, B> =
    apply(ff)

}

@extension
interface FreeCMonad<F> : Monad<FreeCPartialOf<F>> {
  override fun <A, B> Kind<FreeCPartialOf<F>, A>.flatMap(f: (A) -> Kind<FreeCPartialOf<F>, B>): Kind<FreeCPartialOf<F>, B> =
    this.fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: (A) -> Kind<FreeCPartialOf<F>, Either<A, B>>): Kind<FreeCPartialOf<F>, B> =
    FreeC.tailRecM(a) { f(it).fix() }

  override fun <A> just(a: A): Kind<FreeCPartialOf<F>, A> =
    FreeC.just(a)
}

@extension
interface FreeCApplicativeError<F> : ApplicativeError<FreeCPartialOf<F>, Throwable> {
  override fun <A> raiseError(e: Throwable): Kind<FreeCPartialOf<F>, A> =
    FreeC.raiseError(e)

  override fun <A> Kind<FreeCPartialOf<F>, A>.handleErrorWith(f: (Throwable) -> Kind<FreeCPartialOf<F>, A>): Kind<FreeCPartialOf<F>, A> =
    handleErrorW(f)

  override fun <A> just(a: A): Kind<FreeCPartialOf<F>, A> =
    FreeC.just(a)

  override fun <A, B> Kind<FreeCPartialOf<F>, A>.ap(ff: Kind<FreeCPartialOf<F>, (A) -> B>): Kind<FreeCPartialOf<F>, B> =
    apply(ff)

}

@extension
interface FreeCMonadError<F> : MonadError<FreeCPartialOf<F>, Throwable> {
  override fun <A> raiseError(e: Throwable): Kind<FreeCPartialOf<F>, A> =
    FreeC.raiseError(e)

  override fun <A> Kind<FreeCPartialOf<F>, A>.handleErrorWith(f: (Throwable) -> Kind<FreeCPartialOf<F>, A>): Kind<FreeCPartialOf<F>, A> =
    handleErrorW(f)

  override fun <A> just(a: A): Kind<FreeCPartialOf<F>, A> =
    FreeC.just(a)

  override fun <A, B> Kind<FreeCPartialOf<F>, A>.flatMap(f: (A) -> Kind<FreeCPartialOf<F>, B>): Kind<FreeCPartialOf<F>, B> =
    this.fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: (A) -> Kind<FreeCPartialOf<F>, Either<A, B>>): Kind<FreeCPartialOf<F>, B> =
    FreeC.tailRecM(a) { f(it).fix() }

}

@extension
interface FreeCBracket<F> : Bracket<FreeCPartialOf<F>, Throwable>, FreeCMonadError<F> {
  override fun <A, B> Kind<FreeCPartialOf<F>, A>.bracketCase(release: (A, ExitCase<Throwable>) -> Kind<FreeCPartialOf<F>, Unit>, use: (A) -> Kind<FreeCPartialOf<F>, B>): Kind<FreeCPartialOf<F>, B> =
    bracketC(use, release)
}

@extension
interface FreeCMonadDefer<F> : MonadDefer<FreeCPartialOf<F>>, FreeCBracket<F> {
  override fun <A> defer(fa: () -> Kind<FreeCPartialOf<F>, A>): Kind<FreeCPartialOf<F>, A> =
    FreeC.defer(fa)
}

@extension
interface FreeCEq<F, G, A> : Eq<Kind<FreeCPartialOf<F>, A>> {

  fun ME(): MonadError<G, Throwable>

  fun FK(): FunctionK<F, G>

  fun EQFA(): Eq<Kind<G, Option<A>>>

  override fun Kind<FreeCPartialOf<F>, A>.eqv(b: Kind<FreeCPartialOf<F>, A>): Boolean = EQFA().run {
    fix().foldMap(FK(), ME()).eqv(b.fix().foldMap(FK(), ME()))
  }
}
