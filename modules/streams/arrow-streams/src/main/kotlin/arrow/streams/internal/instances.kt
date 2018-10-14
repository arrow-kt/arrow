package arrow.streams.internal

import arrow.Kind
import arrow.core.Either
import arrow.effects.typeclasses.MonadDefer
import arrow.typeclasses.*
import arrow.streams.internal.handleErrorWith as handleErrorW

internal fun <F> FreeC.Companion.functor(): Functor<FreeCPartialOf<F>> = object : FreeCFunctor<F> { }

internal interface FreeCFunctor<F> : Functor<FreeCPartialOf<F>> {
  override fun <A, B> Kind<FreeCPartialOf<F>, A>.map(f: (A) -> B): Kind<FreeCPartialOf<F>, B> =
    this.fix().map(f)
}

internal fun <F> FreeC.Companion.applicative(): Applicative<FreeCPartialOf<F>> = object : FreeCApplicative<F> { }

internal interface FreeCApplicative<F> : Applicative<FreeCPartialOf<F>> {
  override fun <A> just(a: A): Kind<FreeCPartialOf<F>, A> = FreeC.pure(a)

  override fun <A, B> Kind<FreeCPartialOf<F>, A>.ap(ff: Kind<FreeCPartialOf<F>, (A) -> B>): Kind<FreeCPartialOf<F>, B> =
    ff.fix().flatMap { f ->
      this@ap.map(f)
    }

}

internal fun <F> FreeC.Companion.monad(): Monad<FreeCPartialOf<F>> = object : FreeCMonad<F> { }

internal interface FreeCMonad<F> : Monad<FreeCPartialOf<F>> {
  override fun <A, B> Kind<FreeCPartialOf<F>, A>.flatMap(f: (A) -> Kind<FreeCPartialOf<F>, B>): Kind<FreeCPartialOf<F>, B> =
    this.fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: (A) -> Kind<FreeCPartialOf<F>, Either<A, B>>): Kind<FreeCPartialOf<F>, B> =
    f(a).flatMap { it.fold({ l -> tailRecM(l, f) }, { r -> FreeC.pure(r) }) }

  override fun <A> just(a: A): Kind<FreeCPartialOf<F>, A> = FreeC.pure(a)
}

internal fun <F> FreeC.Companion.applicativeError(): ApplicativeError<FreeCPartialOf<F>, Throwable> = object : FreeCApplicativeError<F> { }

internal interface FreeCApplicativeError<F> : ApplicativeError<FreeCPartialOf<F>, Throwable> {
  override fun <A> raiseError(e: Throwable): Kind<FreeCPartialOf<F>, A> = FreeC.raiseError(e)

  override fun <A> Kind<FreeCPartialOf<F>, A>.handleErrorWith(f: (Throwable) -> Kind<FreeCPartialOf<F>, A>): Kind<FreeCPartialOf<F>, A> =
    this.handleErrorW(f)

  override fun <A> just(a: A): Kind<FreeCPartialOf<F>, A> = FreeC.pure(a)

  override fun <A, B> Kind<FreeCPartialOf<F>, A>.ap(ff: Kind<FreeCPartialOf<F>, (A) -> B>): Kind<FreeCPartialOf<F>, B> =
    ff.fix().flatMap { f ->
      this@ap.map(f)
    }

}

internal fun <F> FreeC.Companion.monadError(): MonadError<FreeCPartialOf<F>, Throwable> = object : FreeCMonadError<F> { }

internal interface FreeCMonadError<F> : MonadError<FreeCPartialOf<F>, Throwable> {
  override fun <A> raiseError(e: Throwable): Kind<FreeCPartialOf<F>, A> = FreeC.raiseError(e)

  override fun <A> Kind<FreeCPartialOf<F>, A>.handleErrorWith(f: (Throwable) -> Kind<FreeCPartialOf<F>, A>): Kind<FreeCPartialOf<F>, A> =
    this.handleErrorW(f)

  override fun <A> just(a: A): Kind<FreeCPartialOf<F>, A> = FreeC.pure(a)

  override fun <A, B> Kind<FreeCPartialOf<F>, A>.flatMap(f: (A) -> Kind<FreeCPartialOf<F>, B>): Kind<FreeCPartialOf<F>, B> =
    this.fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: (A) -> Kind<FreeCPartialOf<F>, Either<A, B>>): Kind<FreeCPartialOf<F>, B> =
    f(a).flatMap { it.fold({ l -> tailRecM(l, f) }, { r -> FreeC.pure(r) }) }

}

internal fun <F> FreeC.Companion.monadDefer(): MonadDefer<FreeCPartialOf<F>> = object : FreeCMonadDefer<F> { }

internal interface FreeCMonadDefer<F> : MonadDefer<FreeCPartialOf<F>> {
  override fun <A> defer(fa: () -> Kind<FreeCPartialOf<F>, A>): Kind<FreeCPartialOf<F>, A> = FreeC.suspend() {
    fa()
  }

  override fun <A> raiseError(e: Throwable): Kind<FreeCPartialOf<F>, A> = FreeC.raiseError(e)

  override fun <A> Kind<FreeCPartialOf<F>, A>.handleErrorWith(f: (Throwable) -> Kind<FreeCPartialOf<F>, A>): Kind<FreeCPartialOf<F>, A> =
    this.handleErrorW(f)

  override fun <A> just(a: A): Kind<FreeCPartialOf<F>, A> = FreeC.pure(a)

  override fun <A, B> Kind<FreeCPartialOf<F>, A>.flatMap(f: (A) -> Kind<FreeCPartialOf<F>, B>): Kind<FreeCPartialOf<F>, B> =
    this.fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: (A) -> Kind<FreeCPartialOf<F>, Either<A, B>>): Kind<FreeCPartialOf<F>, B> =
    f(a).flatMap { it.fold({ l -> tailRecM(l, f) }, { r -> FreeC.pure(r) }) }
}