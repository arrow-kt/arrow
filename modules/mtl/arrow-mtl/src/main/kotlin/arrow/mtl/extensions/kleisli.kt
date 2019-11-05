package arrow.mtl.extensions

import arrow.Kind
import arrow.core.Either
import arrow.core.Id
import arrow.core.Ior
import arrow.core.Tuple2
import arrow.core.extensions.id.applicative.applicative
import arrow.core.extensions.id.functor.functor
import arrow.core.extensions.id.monad.monad
import arrow.extension
import arrow.mtl.Kleisli
import arrow.mtl.KleisliOf
import arrow.mtl.KleisliPartialOf
import arrow.mtl.ReaderApi
import arrow.mtl.ReaderPartialOf
import arrow.mtl.extensions.kleisli.applicative.applicative
import arrow.mtl.extensions.kleisli.functor.functor
import arrow.mtl.extensions.kleisli.monad.monad
import arrow.mtl.fix
import arrow.mtl.run
import arrow.mtl.typeclasses.MonadReader
import arrow.typeclasses.Applicative
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.Apply
import arrow.typeclasses.Contravariant
import arrow.typeclasses.Decidable
import arrow.typeclasses.Divide
import arrow.typeclasses.Divisible
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
import arrow.typeclasses.MonadError
import arrow.typeclasses.MonadSyntax
import arrow.typeclasses.MonadThrow
import arrow.undocumented
import arrow.typeclasses.Alternative

@extension
interface KleisliFunctor<D, F> : Functor<KleisliPartialOf<D, F>> {

  fun FF(): Functor<F>

  override fun <A, B> KleisliOf<D, F, A>.map(f: (A) -> B): Kleisli<D, F, B> =
    fix().map(FF(), f)
}

/* TODO change conested to represent λ[α => Kind[α, F, C]] instead of λ[α => Kind[F, α, C]]
@extension
interface KleisliContravariant<D, F> : Contravariant<Conested<Kind<ForKleisli, D>, F>> {
  override fun <A, B> Kind<Conested<Kind<ForKleisli, A>, D>, F>.contramap(f: (B) -> A): Kind<Conested<Kind<ForKleisli, B>, D>, F> =
    counnest().fix().let {

    }
    counnest().fix().local(f).conest()

  fun <A, B> KleisliOf<A, F, D>.contramapC(f: (B) -> A): KleisliOf<B, F, D> =
    conest().contramap(f).counnest()
}
*/

@extension
interface KleisliContravariantInstance<D, F> : Contravariant<KleisliPartialOf<D, F>> {

  fun CF(): Contravariant<F>

  override fun <A, B> Kind<KleisliPartialOf<D, F>, A>.contramap(f: (B) -> A): Kind<KleisliPartialOf<D, F>, B> =
    Kleisli { d -> CF().run { fix().run(d).contramap(f) } }
}

@extension
interface KleisliDivideInstance<D, F> : Divide<KleisliPartialOf<D, F>>, KleisliContravariantInstance<D, F> {

  fun DF(): Divide<F>
  override fun CF(): Contravariant<F> = DF()

  override fun <A, B, Z> divide(fa: Kind<KleisliPartialOf<D, F>, A>, fb: Kind<KleisliPartialOf<D, F>, B>, f: (Z) -> Tuple2<A, B>): Kind<KleisliPartialOf<D, F>, Z> =
    Kleisli { d -> DF().divide(fa.fix().run(d), fb.fix().run(d), f) }
}

@extension
interface KleisliDivisibleInstance<D, F> : Divisible<KleisliPartialOf<D, F>>, KleisliDivideInstance<D, F> {

  fun DFF(): Divisible<F>
  override fun DF(): Divide<F> = DFF()

  override fun <A> conquer(): Kind<KleisliPartialOf<D, F>, A> =
    Kleisli { DFF().conquer() }
}

@extension
interface KleisliDecidableInstance<D, F> : Decidable<KleisliPartialOf<D, F>>, KleisliDivisibleInstance<D, F> {

  fun DFFF(): Decidable<F>
  override fun DFF(): Divisible<F> = DFFF()

  override fun <A, B, Z> choose(fa: Kind<KleisliPartialOf<D, F>, A>, fb: Kind<KleisliPartialOf<D, F>, B>, f: (Z) -> Either<A, B>): Kind<KleisliPartialOf<D, F>, Z> =
    Kleisli { d -> DFFF().choose(fa.fix().run(d), fb.fix().run(d), f) }
}

@extension
interface KleisliApply<D, F> : Apply<KleisliPartialOf<D, F>>, KleisliFunctor<D, F> {

  fun AF(): Applicative<F>

  override fun FF(): Functor<F> = AF()

  override fun <A, B> KleisliOf<D, F, A>.map(f: (A) -> B): Kleisli<D, F, B> =
    fix().map(AF(), f)

  override fun <A, B> KleisliOf<D, F, A>.ap(ff: KleisliOf<D, F, (A) -> B>): Kleisli<D, F, B> =
    fix().ap(AF(), ff)

  override fun <A, B> KleisliOf<D, F, A>.product(fb: KleisliOf<D, F, B>): Kleisli<D, F, Tuple2<A, B>> =
    Kleisli { AF().run { run(it).product(fb.run(it)) } }
}

@extension
interface KleisliApplicative<D, F> : Applicative<KleisliPartialOf<D, F>>, KleisliFunctor<D, F> {

  fun AF(): Applicative<F>

  override fun FF(): Functor<F> = AF()

  override fun <A> just(a: A): Kleisli<D, F, A> =
    Kleisli { AF().just(a) }

  override fun <A, B> KleisliOf<D, F, A>.map(f: (A) -> B): Kleisli<D, F, B> =
    fix().map(AF(), f)

  override fun <A, B> KleisliOf<D, F, A>.ap(ff: KleisliOf<D, F, (A) -> B>): Kleisli<D, F, B> =
    fix().ap(AF(), ff)

  override fun <A, B> KleisliOf<D, F, A>.product(fb: KleisliOf<D, F, B>): Kleisli<D, F, Tuple2<A, B>> =
    Kleisli { AF().run { run(it).product(fb.run(it)) } }
}

@extension
interface KleisliMonad<D, F> : Monad<KleisliPartialOf<D, F>>, KleisliApplicative<D, F> {

  fun MF(): Monad<F>

  override fun AF(): Applicative<F> = MF()

  override fun <A, B> KleisliOf<D, F, A>.map(f: (A) -> B): Kleisli<D, F, B> =
    fix().map(MF(), f)

  override fun <A, B> KleisliOf<D, F, A>.flatMap(f: (A) -> KleisliOf<D, F, B>): Kleisli<D, F, B> =
    fix().flatMap(MF(), f)

  override fun <A, B> KleisliOf<D, F, A>.ap(ff: KleisliOf<D, F, (A) -> B>): Kleisli<D, F, B> =
    fix().ap(MF(), ff)

  override fun <A, B> tailRecM(a: A, f: (A) -> KleisliOf<D, F, Either<A, B>>): Kleisli<D, F, B> =
    Kleisli.tailRecM(MF(), a, f)
}

@extension
interface KleisliApplicativeError<D, F, E> : ApplicativeError<KleisliPartialOf<D, F>, E>, KleisliApplicative<D, F> {

  fun AE(): ApplicativeError<F, E>

  override fun AF(): Applicative<F> = AE()

  override fun <A> KleisliOf<D, F, A>.handleErrorWith(f: (E) -> KleisliOf<D, F, A>): Kleisli<D, F, A> =
    fix().handleErrorWith(AE(), f)

  override fun <A> raiseError(e: E): Kleisli<D, F, A> =
    Kleisli.raiseError(AE(), e)
}

@extension
interface KleisliMonadError<D, F, E> : MonadError<KleisliPartialOf<D, F>, E>, KleisliApplicativeError<D, F, E>, KleisliMonad<D, F> {

  fun ME(): MonadError<F, E>

  override fun MF(): Monad<F> = ME()

  override fun AE(): ApplicativeError<F, E> = ME()

  override fun AF(): Applicative<F> = ME()
}

@extension
@undocumented
interface KleisliMonadThrow<D, F> : MonadThrow<KleisliPartialOf<D, F>>, KleisliMonadError<D, F, Throwable> {
  override fun ME(): MonadError<F, Throwable>
}

@extension
interface KleisliAlternative<D, F> : Alternative<KleisliPartialOf<D, F>>, KleisliApplicative<D, F> {
  override fun AF(): Applicative<F> = AL()
  fun AL(): Alternative<F>

  override fun <A> empty(): Kind<KleisliPartialOf<D, F>, A> = Kleisli { AL().empty() }
  override fun <A> Kind<KleisliPartialOf<D, F>, A>.orElse(b: Kind<KleisliPartialOf<D, F>, A>): Kind<KleisliPartialOf<D, F>, A> =
    Kleisli { d -> AL().run { run(d).orElse(b.run(d)) } }
}

@extension
interface KleisliMonadReader<D, F> : MonadReader<KleisliPartialOf<D, F>, D>, KleisliMonad<D, F> {

  override fun MF(): Monad<F>

  override fun ask(): Kleisli<D, F, D> = Kleisli { MF().just(it) }

  override fun <A> Kind<KleisliPartialOf<D, F>, A>.local(f: (D) -> D): Kleisli<D, F, A> = fix().local(f)
}

class KleisliMtlContext<D, F, E>(val MF: MonadError<F, E>) : KleisliMonadReader<D, F>, KleisliMonadError<D, F, E> {

  override fun MF(): Monad<F> = MF

  override fun ME(): MonadError<F, E> = MF
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

fun <D, F, A> Ior.Companion.fx(MF: Monad<F>, c: suspend MonadSyntax<KleisliPartialOf<D, F>>.() -> A): Kleisli<D, F, A> =
  Kleisli.monad<D, F>(MF).fx.monad(c).fix()
