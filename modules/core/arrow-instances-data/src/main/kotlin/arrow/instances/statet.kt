package arrow.instances

import arrow.Kind
import arrow.core.*
import arrow.data.*
import arrow.extension
import arrow.instances.syntax.id.monad.monad
import arrow.instances.syntax.statet.applicative.applicative
import arrow.instances.syntax.statet.functor.functor
import arrow.instances.syntax.statet.monad.monad
import arrow.typeclasses.*

@extension
interface StateTFunctorInstance<F, S> : Functor<StateTPartialOf<F, S>> {

  fun FF(): Functor<F>

  override fun <A, B> Kind<StateTPartialOf<F, S>, A>.map(f: (A) -> B): StateT<F, S, B> =
    fix().map(FF(), f)

}

@extension
interface StateTApplicativeInstance<F, S> : Applicative<StateTPartialOf<F, S>>, StateTFunctorInstance<F, S> {

  fun MF(): Monad<F>

  override fun FF(): Functor<F> = MF()

  override fun <A, B> Kind<StateTPartialOf<F, S>, A>.map(f: (A) -> B): StateT<F, S, B> =
    fix().map(MF(), f)

  override fun <A> just(a: A): StateT<F, S, A> =
    StateT(MF().just({ s: S -> MF().just(Tuple2(s, a)) }))

  override fun <A, B> Kind<StateTPartialOf<F, S>, A>.ap(ff: Kind<StateTPartialOf<F, S>, (A) -> B>): StateT<F, S, B> =
    fix().ap(MF(), ff)

  override fun <A, B> Kind<StateTPartialOf<F, S>, A>.product(fb: Kind<StateTPartialOf<F, S>, B>): StateT<F, S, Tuple2<A, B>> =
    fix().product(MF(), fb.fix())

}

@extension
interface StateTMonadInstance<F, S> : Monad<StateTPartialOf<F, S>>, StateTApplicativeInstance<F, S> {

  override fun MF(): Monad<F>

  override fun <A, B> Kind<StateTPartialOf<F, S>, A>.map(f: (A) -> B): StateT<F, S, B> =
    fix().map(MF(), f)

  override fun <A, B> Kind<StateTPartialOf<F, S>, A>.flatMap(f: (A) -> Kind<StateTPartialOf<F, S>, B>): StateT<F, S, B> =
    fix().flatMap(MF(), f)

  override fun <A, B> tailRecM(a: A, f: (A) -> StateTOf<F, S, Either<A, B>>): StateT<F, S, B> =
    StateT.tailRecM(MF(), a, f)

  override fun <A, B> Kind<StateTPartialOf<F, S>, A>.ap(ff: Kind<StateTPartialOf<F, S>, (A) -> B>): StateT<F, S, B> =
    ff.fix().map2(MF(), fix(), { f, a -> f(a) })

}

@extension
interface StateTSemigroupKInstance<F, S> : SemigroupK<StateTPartialOf<F, S>> {

  fun FF(): Monad<F>

  fun SS(): SemigroupK<F>

  override fun <A> Kind<StateTPartialOf<F, S>, A>.combineK(y: Kind<StateTPartialOf<F, S>, A>): StateT<F, S, A> =
    fix().combineK(FF(), SS(), y)

}

@extension
interface StateTApplicativeErrorInstance<F, S, E> : ApplicativeError<StateTPartialOf<F, S>, E>, StateTApplicativeInstance<F, S> {

  fun ME(): MonadError<F, E>

  override fun FF(): Functor<F> = ME()

  override fun MF(): Monad<F> = ME()

  override fun <A> raiseError(e: E): Kind<StateTPartialOf<F, S>, A> = StateT.lift(ME(), ME().raiseError(e))

  override fun <A> Kind<StateTPartialOf<F, S>, A>.handleErrorWith(f: (E) -> Kind<StateTPartialOf<F, S>, A>): StateT<F, S, A> =
    StateT(ME().just({ s -> ME().run { runM(ME(), s).handleErrorWith({ e -> f(e).runM(ME(), s) }) } }))
}

@extension
interface StateTMonadErrorInstance<F, S, E> : MonadError<StateTPartialOf<F, S>, E>, StateTApplicativeErrorInstance<F, S, E>, StateTMonadInstance<F, S> {

  override fun MF(): Monad<F> = ME()

  override fun ME(): MonadError<F, E>

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

class StateTContext<F, S, E>(val ME: MonadError<F, E>) : StateTMonadErrorInstance<F, S, E> {
  override fun MF(): Monad<F> = ME()

  override fun ME(): MonadError<F, E> = ME
}

class StateTContextPartiallyApplied<F, S, E>(val ME: MonadError<F, E>) {
  infix fun <A> extensions(f: StateTContext<F, S, E>.() -> A): A =
    f(StateTContext(ME))
}

fun <F, S, E> ForStateT(ME: MonadError<F, E>): StateTContextPartiallyApplied<F, S, E> =
  StateTContextPartiallyApplied(ME)

class StateTMonadContext<S> : StateTMonadInstance<ForId, S> {
  override fun MF(): Monad<ForId> = Id.monad()
}

class StateContextPartiallyApplied<S>() {
  infix fun <A> extensions(f: StateTMonadContext<S>.() -> A): A =
    f(StateTMonadContext())
}

fun <S> ForState(): StateContextPartiallyApplied<S> =
  StateContextPartiallyApplied()
