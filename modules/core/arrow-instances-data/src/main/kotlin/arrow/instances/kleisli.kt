package arrow.instances

import arrow.Kind
import arrow.core.*
import arrow.data.*
import arrow.deprecation.ExtensionsDSLDeprecated
import arrow.extension
import arrow.instances.id.applicative.applicative
import arrow.instances.id.functor.functor
import arrow.instances.id.monad.monad
import arrow.instances.kleisli.applicative.applicative
import arrow.instances.kleisli.functor.functor
import arrow.instances.kleisli.monad.monad
import arrow.typeclasses.*

@extension
interface KleisliFunctorInstance<F, D> : Functor<KleisliPartialOf<F, D>> {

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
interface KleisliApplicativeInstance<F, D> : Applicative<KleisliPartialOf<F, D>>, KleisliFunctorInstance<F, D> {

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
interface KleisliMonadInstance<F, D> : Monad<KleisliPartialOf<F, D>>, KleisliApplicativeInstance<F, D> {

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
interface KleisliApplicativeErrorInstance<F, D, E> : ApplicativeError<KleisliPartialOf<F, D>, E>, KleisliApplicativeInstance<F, D> {

  fun AE(): ApplicativeError<F, E>

  override fun AF(): Applicative<F> = AE()

  override fun <A> KleisliOf<F, D, A>.handleErrorWith(f: (E) -> KleisliOf<F, D, A>): Kleisli<F, D, A> =
    fix().handleErrorWith(AE(), f)

  override fun <A> raiseError(e: E): Kleisli<F, D, A> =
    Kleisli.raiseError(AE(), e)
}

@extension
interface KleisliMonadErrorInstance<F, D, E> : MonadError<KleisliPartialOf<F, D>, E>, KleisliApplicativeErrorInstance<F, D, E>, KleisliMonadInstance<F, D> {

  fun ME(): MonadError<F, E>

  override fun MF(): Monad<F> = ME()

  override fun AE(): ApplicativeError<F, E> = ME()

  override fun AF(): Applicative<F> = ME()
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

class ReaderContext<D> : KleisliMonadInstance<ForId, D> {
  override fun MF(): Monad<ForId> = Id.monad()
}

class ReaderContextPartiallyApplied<L> {
  @Deprecated(ExtensionsDSLDeprecated)
  inline fun <A> extensions(f: ReaderContext<L>.() -> A): A =
    f(ReaderContext())
}

fun <D> Reader(): ReaderContextPartiallyApplied<D> =
  ReaderContextPartiallyApplied()

class KleisliContext<F, D, E>(val ME: MonadError<F, E>) : KleisliMonadErrorInstance<F, D, E> {
  override fun ME(): MonadError<F, E> = ME
}

class KleisliContextPartiallyApplied<F, D, E>(val MF: MonadError<F, E>) {
  @Deprecated(ExtensionsDSLDeprecated)
  infix fun <A> extensions(f: KleisliContext<F, D, E>.() -> A): A =
    f(KleisliContext(MF))
}

fun <F, D, E> ForKleisli(MF: MonadError<F, E>): KleisliContextPartiallyApplied<F, D, E> =
  KleisliContextPartiallyApplied(MF)
