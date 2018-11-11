package arrow.instances

import arrow.Kind
import arrow.core.Either
import arrow.core.ForId
import arrow.core.Id
import arrow.core.Tuple2
import arrow.core.andThen
import arrow.data.ForKleisli
import arrow.data.Kleisli
import arrow.data.KleisliOf
import arrow.data.KleisliPartialOf
import arrow.data.ReaderApi
import arrow.data.ReaderPartialOf
import arrow.data.fix
import arrow.deprecation.ExtensionsDSLDeprecated
import arrow.extension
import arrow.instances.id.applicative.applicative
import arrow.instances.id.functor.functor
import arrow.instances.id.monad.monad
import arrow.instances.kleisli.applicative.applicative
import arrow.instances.kleisli.functor.functor
import arrow.instances.kleisli.monad.monad
import arrow.typeclasses.Applicative
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.Conested
import arrow.typeclasses.Contravariant
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
import arrow.typeclasses.MonadError
import arrow.typeclasses.conest
import arrow.typeclasses.counnest

@extension
interface KleisliFunctorInstance<F, D> : Functor<KleisliPartialOf<F, D>> {

  fun FFF(): Functor<F>

  override fun <A, B> Kind<KleisliPartialOf<F, D>, A>.map(f: (A) -> B): Kleisli<F, D, B> = fix().map(FFF(), f)
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

  override fun FFF(): Applicative<F>

  override fun <A> just(a: A): Kleisli<F, D, A> = Kleisli { FFF().just(a) }

  override fun <A, B> Kind<KleisliPartialOf<F, D>, A>.map(f: (A) -> B): Kleisli<F, D, B> =
    fix().map(FFF(), f)

  override fun <A, B> Kind<KleisliPartialOf<F, D>, A>.ap(ff: Kind<KleisliPartialOf<F, D>, (A) -> B>): Kleisli<F, D, B> =
    fix().ap(FFF(), ff)

  override fun <A, B> Kind<KleisliPartialOf<F, D>, A>.product(fb: Kind<KleisliPartialOf<F, D>, B>): Kleisli<F, D, Tuple2<A, B>> =
    Kleisli { FFF().run { fix().run(it).product(fb.fix().run(it)) } }
}

@extension
interface KleisliMonadInstance<F, D> : Monad<KleisliPartialOf<F, D>>, KleisliApplicativeInstance<F, D> {

  override fun FFF(): Monad<F>

  override fun <A, B> Kind<KleisliPartialOf<F, D>, A>.map(f: (A) -> B): Kleisli<F, D, B> =
    fix().map(FFF(), f)

  override fun <A, B> Kind<KleisliPartialOf<F, D>, A>.flatMap(f: (A) -> Kind<KleisliPartialOf<F, D>, B>): Kleisli<F, D, B> =
    fix().flatMap(FFF(), f.andThen { it.fix() })

  override fun <A, B> Kind<KleisliPartialOf<F, D>, A>.ap(ff: Kind<KleisliPartialOf<F, D>, (A) -> B>): Kleisli<F, D, B> =
    fix().ap(FFF(), ff)

  override fun <A, B> tailRecM(a: A, f: (A) -> KleisliOf<F, D, Either<A, B>>): Kleisli<F, D, B> =
    Kleisli.tailRecM(FFF(), a, f)
}

@extension
interface KleisliApplicativeErrorInstance<F, D, E> : ApplicativeError<KleisliPartialOf<F, D>, E>, KleisliApplicativeInstance<F, D> {

  override fun FFF(): ApplicativeError<F, E>

  override fun <A> Kind<KleisliPartialOf<F, D>, A>.handleErrorWith(f: (E) -> Kind<KleisliPartialOf<F, D>, A>): Kleisli<F, D, A> =
    fix().handleErrorWith(FFF(), f)

  override fun <A> raiseError(e: E): Kleisli<F, D, A> =
    Kleisli.raiseError(FFF(), e)
}

@extension
interface KleisliMonadErrorInstance<F, D, E> : MonadError<KleisliPartialOf<F, D>, E>, KleisliApplicativeErrorInstance<F, D, E>, KleisliMonadInstance<F, D> {

  override fun FFF(): MonadError<F, E>
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
  override fun FFF(): Monad<ForId> = Id.monad()
}

class ReaderContextPartiallyApplied<L> {
  @Deprecated(ExtensionsDSLDeprecated)
  inline fun <A> extensions(f: ReaderContext<L>.() -> A): A =
    f(ReaderContext())
}

fun <D> Reader(): ReaderContextPartiallyApplied<D> =
  ReaderContextPartiallyApplied()

class KleisliContext<F, D, E>(val ME: MonadError<F, E>) : KleisliMonadErrorInstance<F, D, E> {
  override fun FFF(): MonadError<F, E> = ME
}

class KleisliContextPartiallyApplied<F, D, E>(val MF: MonadError<F, E>) {
  @Deprecated(ExtensionsDSLDeprecated)
  infix fun <A> extensions(f: KleisliContext<F, D, E>.() -> A): A =
    f(KleisliContext(MF))
}

fun <F, D, E> ForKleisli(MF: MonadError<F, E>): KleisliContextPartiallyApplied<F, D, E> =
  KleisliContextPartiallyApplied(MF)
