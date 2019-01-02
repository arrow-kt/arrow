package arrow.data.extensions

import arrow.core.*
import arrow.data.*

import arrow.extension
import arrow.core.extensions.id.monad.monad
import arrow.data.extensions.statet.applicative.applicative
import arrow.data.extensions.statet.functor.functor
import arrow.data.extensions.statet.monad.monad
import arrow.typeclasses.*

@extension
interface StateTFunctorInstance<F, S> : Functor<StateTPartialOf<F, S>> {

  fun FF(): Functor<F>

  override fun <A, B> StateTOf<F, S, A>.map(f: (A) -> B): StateT<F, S, B> =
    fix().map(FF(), f)

}

@extension
interface StateTApplicativeInstance<F, S> : Applicative<StateTPartialOf<F, S>>, StateTFunctorInstance<F, S> {

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
interface StateTMonadInstance<F, S> : Monad<StateTPartialOf<F, S>>, StateTApplicativeInstance<F, S> {

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
interface StateTSemigroupKInstance<F, S> : SemigroupK<StateTPartialOf<F, S>> {

  fun FF(): Monad<F>

  fun SS(): SemigroupK<F>

  override fun <A> StateTOf<F, S, A>.combineK(y: StateTOf<F, S, A>): StateT<F, S, A> =
    fix().combineK(FF(), SS(), y)

}

@extension
interface StateTApplicativeErrorInstance<F, S, E> : ApplicativeError<StateTPartialOf<F, S>, E>, StateTApplicativeInstance<F, S> {

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
interface StateTMonadErrorInstance<F, S, E> : MonadError<StateTPartialOf<F, S>, E>, StateTApplicativeErrorInstance<F, S, E>, StateTMonadInstance<F, S> {

  override fun MF(): Monad<F> = ME()

  override fun ME(): MonadError<F, E>

}

fun <F, S, E> StateT.Companion.monadError(ME: MonadError<F, E>): MonadError<StateTPartialOf<F, S>, E> = object : StateTMonadErrorInstance<F, S, E> {
  override fun ME(): MonadError<F, E> = ME
}

interface StateTMonadThrowInstance<F, S> : MonadThrow<StateTPartialOf<F, S>>, StateTMonadErrorInstance<F, S, Throwable>

fun <F, S> StateT.Companion.monadThrow(ME: MonadError<F, Throwable>): MonadThrow<StateTPartialOf<F, S>> = object : StateTMonadThrowInstance<F, S> {
  override fun ME(): MonadError<F, Throwable> = ME
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
