package arrow.data.extensions

import arrow.Kind
import arrow.core.*
import arrow.data.*
import arrow.extension
import arrow.core.extensions.id.applicative.applicative
import arrow.core.extensions.id.functor.functor
import arrow.core.extensions.id.monad.monad
import arrow.data.extensions.ior.monad.monad
import arrow.data.extensions.kleisli.applicative.applicative
import arrow.data.extensions.kleisli.functor.functor
import arrow.data.extensions.kleisli.monad.monad
import arrow.typeclasses.*
import arrow.typeclasses.suspended.monad.Fx
import arrow.undocumented

@extension
interface KleisliFunctor<F, D> : Functor<KleisliPartialOf<F, D>> {

  fun FF(): Functor<F>

  override fun <A, B> KleisliOf<F, D, A>.map(f: (A) -> B): Kleisli<F, D, B> =
    fix().map(FF(), f)
}

@extension
interface KleisliContravariant<F, D> : Contravariant<Conested<Kind<ForKleisli, F>, D>> {
  override fun <A, B> Kind<Conested<Kind<ForKleisli, F>, D>, A>.contramap(f: (B) -> A): Kind<Conested<Kind<ForKleisli, F>, D>, B> =
    counnest().fix().local(f).conest()

  fun <A, B> KleisliOf<F, A, D>.contramapC(f: (B) -> A): KleisliOf<F, B, D> =
    conest().contramap(f).counnest()
}

@extension
interface KleisliApplicative<F, D> : Applicative<KleisliPartialOf<F, D>>, KleisliFunctor<F, D> {

  fun AF(): Applicative<F>

  override fun FF(): Functor<F> = AF()

  override fun <A> just(a: A): Kleisli<F, D, A> =
    Kleisli { AF().just(a) }

  override fun <A, B> KleisliOf<F, D, A>.map(f: (A) -> B): Kleisli<F, D, B> =
    fix().map(AF(), f)

  override fun <A, B> KleisliOf<F, D, A>.ap(ff: KleisliOf<F, D, (A) -> B>): Kleisli<F, D, B> =
    fix().ap(AF(), ff)

  override fun <A, B> KleisliOf<F, D, A>.product(fb: KleisliOf<F, D, B>): Kleisli<F, D, Tuple2<A, B>> =
    Kleisli { AF().run { run(it).product(fb.run(it)) } }
}

@extension
interface KleisliMonad<F, D> : Monad<KleisliPartialOf<F, D>>, KleisliApplicative<F, D> {

  fun MF(): Monad<F>

  override fun AF(): Applicative<F> = MF()

  override fun <A, B> KleisliOf<F, D, A>.map(f: (A) -> B): Kleisli<F, D, B> =
    fix().map(MF(), f)

  override fun <A, B> KleisliOf<F, D, A>.flatMap(f: (A) -> KleisliOf<F, D, B>): Kleisli<F, D, B> =
    fix().flatMap(MF(), f)

  override fun <A, B> KleisliOf<F, D, A>.ap(ff: KleisliOf<F, D, (A) -> B>): Kleisli<F, D, B> =
    fix().ap(MF(), ff)

  override fun <A, B> tailRecM(a: A, f: (A) -> KleisliOf<F, D, Either<A, B>>): Kleisli<F, D, B> =
    Kleisli.tailRecM(MF(), a, f)
}

@extension
interface KleisliApplicativeError<F, D, E> : ApplicativeError<KleisliPartialOf<F, D>, E>, KleisliApplicative<F, D> {

  fun AE(): ApplicativeError<F, E>

  override fun AF(): Applicative<F> = AE()

  override fun <A> KleisliOf<F, D, A>.handleErrorWith(f: (E) -> KleisliOf<F, D, A>): Kleisli<F, D, A> =
    fix().handleErrorWith(AE(), f)

  override fun <A> raiseError(e: E): Kleisli<F, D, A> =
    Kleisli.raiseError(AE(), e)
}

@extension
interface KleisliMonadError<F, D, E> : MonadError<KleisliPartialOf<F, D>, E>, KleisliApplicativeError<F, D, E>, KleisliMonad<F, D> {

  fun ME(): MonadError<F, E>

  override fun MF(): Monad<F> = ME()

  override fun AE(): ApplicativeError<F, E> = ME()

  override fun AF(): Applicative<F> = ME()

}

@extension
@undocumented
interface KleisliMonadThrow<F, D> : MonadThrow<KleisliPartialOf<F, D>>, KleisliMonadError<F, D, Throwable> {
  override fun ME(): MonadError<F, Throwable>
}

/**
 * Alias for [Kleisli] for [Id]
 */
fun <D> ReaderApi.functor(): Functor<ReaderPartialOf<D>> = Kleisli.functor(Id.functor())

/**
 * Alias for [Kleisli] for [Id]
 */
fun <D> ReaderApi.applicative(): Applicative<ReaderPartialOf<D>> = Kleisli.applicative(Id.applicative())

/**
 * Alias for [Kleisli] for [Id]
 */
fun <D> ReaderApi.monad(): Monad<ReaderPartialOf<D>> = Kleisli.monad(Id.monad())

@extension
@undocumented
interface KleisliFx<F, D> : Fx<KleisliPartialOf<F, D>> {

  fun MF(): Monad<F>

  override fun monad(): Monad<KleisliPartialOf<F, D>> =
    Kleisli.monad(MF())

}