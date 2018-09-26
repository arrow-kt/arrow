package arrow.instances

import arrow.Kind
import arrow.core.Either
import arrow.core.ForId
import arrow.core.Id
import arrow.core.Tuple2
import arrow.core.andThen
import arrow.core.applicative
import arrow.core.functor
import arrow.core.monad
import arrow.data.ForKleisli
import arrow.data.Kleisli
import arrow.data.KleisliOf
import arrow.data.KleisliPartialOf
import arrow.data.ReaderApi
import arrow.data.ReaderPartialOf
import arrow.data.applicative
import arrow.data.fix
import arrow.data.functor
import arrow.data.monad
import arrow.deprecation.ExtensionsDSLDeprecated
import arrow.effects.typeclasses.Bracket
import arrow.effects.typeclasses.ExitCase
import arrow.extension
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

  fun FF(): Functor<F>

  override fun <A, B> Kind<KleisliPartialOf<F, D>, A>.map(f: (A) -> B): Kleisli<F, D, B> = fix().map(FF(), f)
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

  override fun <A> just(a: A): Kleisli<F, D, A> = Kleisli { AF().just(a) }

  override fun <A, B> Kind<KleisliPartialOf<F, D>, A>.map(f: (A) -> B): Kleisli<F, D, B> =
    fix().map(AF(), f)

  override fun <A, B> Kind<KleisliPartialOf<F, D>, A>.ap(ff: Kind<KleisliPartialOf<F, D>, (A) -> B>): Kleisli<F, D, B> =
    fix().ap(AF(), ff)

  override fun <A, B> Kind<KleisliPartialOf<F, D>, A>.product(fb: Kind<KleisliPartialOf<F, D>, B>): Kleisli<F, D, Tuple2<A, B>> =
    Kleisli({ AF().run { fix().run(it).product(fb.fix().run(it)) } })
}

@extension
interface KleisliMonadInstance<F, D> : Monad<KleisliPartialOf<F, D>>, KleisliApplicativeInstance<F, D> {

  fun MF(): Monad<F>

  override fun AF(): Applicative<F> = MF()

  override fun <A, B> Kind<KleisliPartialOf<F, D>, A>.map(f: (A) -> B): Kleisli<F, D, B> =
    fix().map(MF(), f)

  override fun <A, B> Kind<KleisliPartialOf<F, D>, A>.flatMap(f: (A) -> Kind<KleisliPartialOf<F, D>, B>): Kleisli<F, D, B> =
    fix().flatMap(MF(), f.andThen { it.fix() })

  override fun <A, B> Kind<KleisliPartialOf<F, D>, A>.ap(ff: Kind<KleisliPartialOf<F, D>, (A) -> B>): Kleisli<F, D, B> =
    fix().ap(MF(), ff)

  override fun <A, B> tailRecM(a: A, f: (A) -> KleisliOf<F, D, Either<A, B>>): Kleisli<F, D, B> =
    Kleisli.tailRecM(MF(), a, f)

}

@extension
interface KleisliApplicativeErrorInstance<F, D, E> : ApplicativeError<KleisliPartialOf<F, D>, E>, KleisliApplicativeInstance<F, D> {

  fun AE(): ApplicativeError<F, E>

  override fun AF(): Applicative<F> = AE()

  override fun <A> Kind<KleisliPartialOf<F, D>, A>.handleErrorWith(f: (E) -> Kind<KleisliPartialOf<F, D>, A>): Kleisli<F, D, A> =
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

@extension
interface KleisliBracketInstance<F, R, E> : Bracket<KleisliPartialOf<F, R>, E>, KleisliMonadErrorInstance<F, R, E> {

  override fun FF(): Bracket<F, E>

  override fun <A, B> Kind<KleisliPartialOf<F, R>, A>.flatMap(f: (A) -> Kind<KleisliPartialOf<F, R>, B>): Kleisli<F, R, B> =
    FF().run {
      this@flatMap.flatMap(f)
    }

  override fun <A, B> Kind<KleisliPartialOf<F, R>, A>.bracketCase(
    use: (A) -> Kind<KleisliPartialOf<F, R>, B>,
    release: (A, ExitCase<E>) -> Kind<KleisliPartialOf<F, R>, Unit>
  ): Kleisli<F, R, B> =
    FF().run {
      Kleisli { r ->
        this@bracketCase.fix().run(r).bracketCase({ a ->
          use(a).fix().run(r)
        }, { a, br ->
          release(a, br).fix().run(r)
        })
      }
    }

  override fun <A> Kind<KleisliPartialOf<F, R>, A>.uncancelable(): Kleisli<F, R, A> =
    Kleisli { r -> FF().run { this@uncancelable.fix().run(r).uncancelable() } }
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
