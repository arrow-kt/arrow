package arrow.data.extensions

import arrow.Kind
import arrow.core.*
import arrow.data.*

import arrow.extension
import arrow.core.extensions.id.monad.monad
import arrow.data.extensions.statet.applicative.applicative
import arrow.data.extensions.statet.functor.functor
import arrow.data.extensions.statet.monad.monad
import arrow.typeclasses.*
import arrow.typeclasses.suspended.monad.commutative.safe.Fx
import arrow.undocumented

@extension
@undocumented
interface StateTFunctor<F, S> : Functor<StateTPartialOf<F, S>> {

  fun FF(): Functor<F>

  override fun <A, B> StateTOf<F, S, A>.map(f: (A) -> B): StateT<F, S, B> =
    fix().map(FF(), f)

}

@extension
@undocumented
interface StateTApplicative<F, S> : Applicative<StateTPartialOf<F, S>>, StateTFunctor<F, S> {

  fun MF(): Monad<F>

  override fun FF(): Functor<F> = MF()

  override fun <A, B> StateTOf<F, S, A>.map(f: (A) -> B): StateT<F, S, B> =
    fix().map(MF(), f)

  override fun <A> just(a: A): StateT<F, S, A> =
    StateT(MF().just({ s: S -> MF().just(Tuple2(s, a)) }))

  override fun <A, B> StateTOf<F, S, A>.ap(ff: StateTOf<F, S, (A) -> B>): StateT<F, S, B> =
    fix().ap(MF(), ff)

  override fun <A, B> StateTOf<F, S, A>.product(fb: StateTOf<F, S, B>): StateT<F, S, Tuple2<A, B>> =
    fix().product(MF(), fb)

}

@extension
@undocumented
interface StateTMonad<F, S> : Monad<StateTPartialOf<F, S>>, StateTApplicative<F, S> {

  override fun MF(): Monad<F>

  override fun <A, B> StateTOf<F, S, A>.map(f: (A) -> B): StateT<F, S, B> =
    fix().map(MF(), f)

  override fun <A, B> StateTOf<F, S, A>.flatMap(f: (A) -> StateTOf<F, S, B>): StateT<F, S, B> =
    fix().flatMap(MF(), f)

  override fun <A, B> tailRecM(a: A, f: (A) -> StateTOf<F, S, Either<A, B>>): StateT<F, S, B> =
    StateT.tailRecM(MF(), a, f)

  override fun <A, B> StateTOf<F, S, A>.ap(ff: StateTOf<F, S, (A) -> B>): StateT<F, S, B> =
    ff.fix().map2(MF(), this) { f, a -> f(a) }

}

@extension
@undocumented
interface StateTSemigroupK<F, S> : SemigroupK<StateTPartialOf<F, S>> {

  fun FF(): Monad<F>

  fun SS(): SemigroupK<F>

  override fun <A> StateTOf<F, S, A>.combineK(y: StateTOf<F, S, A>): StateT<F, S, A> =
    fix().combineK(FF(), SS(), y)

}

@extension
@undocumented
interface StateTApplicativeError<F, S, E> : ApplicativeError<StateTPartialOf<F, S>, E>, StateTApplicative<F, S> {

  fun ME(): MonadError<F, E>

  override fun FF(): Functor<F> = ME()

  override fun MF(): Monad<F> = ME()

  override fun <A> raiseError(e: E): StateTOf<F, S, A> = ME().run {
    StateT.liftF(this, raiseError(e))
  }

  override fun <A> StateTOf<F, S, A>.handleErrorWith(f: (E) -> StateTOf<F, S, A>): StateT<F, S, A> = ME().run {
    State(this) { s ->
      runM(this, s).handleErrorWith { e ->
        f(e).runM(this, s)
      }
    }
  }

}

@extension
@undocumented
interface StateTMonadError<F, S, E> : MonadError<StateTPartialOf<F, S>, E>, StateTApplicativeError<F, S, E>, StateTMonad<F, S> {

  override fun ME(): MonadError<F, E>

  override fun MF(): Monad<F> = ME()

}

@extension
@undocumented
interface StateTMonadThrow<F, S> : MonadThrow<StateTPartialOf<F, S>>, StateTMonadError<F, S, Throwable> {
  override fun ME(): MonadError<F, Throwable>
}

@extension
@undocumented
interface StateTContravariantInstance<F, S> : Contravariant<StateTPartialOf<F, S>> {

  fun CF(): Contravariant<F>

  fun MF(): Monad<F>

  override fun <A, B> Kind<StateTPartialOf<F, S>, A>.contramap(f: (B) -> A): Kind<StateTPartialOf<F, S>, B> =
    StateT(MF()) { s ->
      CF().run {
        runM(MF(), s).contramap { (s, b) ->
          s toT f(b)
        }
      }
    }
}

@extension
@undocumented
interface StateTDivideInstance<F, S> : Divide<StateTPartialOf<F, S>>, StateTContravariantInstance<F, S> {

  fun DF(): Divide<F>
  override fun CF(): Contravariant<F> = DF()

  fun MFF(): Monad<F>
  override fun MF(): Monad<F> = MFF()

  override fun <A, B, Z> divide(fa: Kind<StateTPartialOf<F, S>, A>, fb: Kind<StateTPartialOf<F, S>, B>, f: (Z) -> Tuple2<A, B>): Kind<StateTPartialOf<F, S>, Z> =
    StateT(MF()) { s ->
      DF().divide(fa.runM(MF(), s), fb.runM(MF(), s)) { (s, z) ->
        val (a, b) = f(z)
        (s toT a) toT (s toT b)
      }
    }
}

@extension
@undocumented
interface StateTDivisibleInstance<F, S> : Divisible<StateTPartialOf<F, S>>, StateTDivideInstance<F, S> {
  fun DFF(): Divisible<F>
  override fun DF(): Divide<F> = DFF()
  fun MFFF(): Monad<F>
  override fun MFF(): Monad<F> = MFFF()

  override fun <A> conquer(): Kind<StateTPartialOf<F, S>, A> =
    StateT(MF()) { DFF().conquer() }
}

@extension
@undocumented
interface StateTDecidableInstante<F, S> : Decidable<StateTPartialOf<F, S>>, StateTDivisibleInstance<F, S> {
  fun DFFF(): Decidable<F>
  override fun DFF(): Divisible<F> = DFFF()
  fun MFFFF(): Monad<F>
  override fun MFFF(): Monad<F> = MFFFF()

  override fun <A, B, Z> choose(fa: Kind<StateTPartialOf<F, S>, A>, fb: Kind<StateTPartialOf<F, S>, B>, f: (Z) -> Either<A, B>): Kind<StateTPartialOf<F, S>, Z> =
    StateT(MF()) { s ->
      DFFF().choose(fa.runM(MF(), s), fb.runM(MF(), s)) { (s, z) ->
        f(z).fold({ a ->
          (s toT a).left()
        }, { b ->
          (s toT b).right()
        })
      }
    }
}

/**
 * Alias for[StateT.Companion.applicative]
 */
fun <S> StateApi.applicative(): Applicative<StateTPartialOf<ForId, S>> = StateT.applicative(Id.monad())

/**
 * Alias for [StateT.Companion.functor]
 */
fun <S> StateApi.functor(): Functor<StateTPartialOf<ForId, S>> = StateT.functor(Id.monad())

/**
 * Alias for [StateT.Companion.monad]
 */
fun <S> StateApi.monad(): Monad<StateTPartialOf<ForId, S>> = StateT.monad(Id.monad())

@extension
@undocumented
interface StateTFx<F, S> : Fx<StateTPartialOf<F, S>> {

  fun M() : Monad<F>

  override fun monad(): Monad<StateTPartialOf<F, S>> =
    StateT.monad(M())

}

@extension
@undocumented
interface StateFx<S> : StateTFx<ForId, S> {

  override fun M(): Monad<ForId> =
    Id.monad()

}