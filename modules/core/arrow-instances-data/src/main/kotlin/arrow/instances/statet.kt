package arrow.instances

import arrow.Kind
import arrow.core.*
import arrow.data.*
import arrow.instance
import arrow.typeclasses.*

@instance(IndexedStateT::class)
interface IndexedStateTFunctorInstance<F, S> : Functor<IndexedStateTPartialOf<F, S, S>> {

  fun FF(): Functor<F>

  override fun <A, B> Kind<IndexedStateTPartialOf<F, S, S>, A>.map(f: (A) -> B): Kind<IndexedStateTPartialOf<F, S, S>, B> =
    this.fix().map(FF(), f)

}

@instance(IndexedStateT::class)
interface IndexedStateTApplicativeInstance<F, S> : IndexedStateTFunctorInstance<F, S>, Applicative<IndexedStateTPartialOf<F, S, S>> {

  override fun FF(): Monad<F>

  override fun <A> just(a: A): Kind<IndexedStateTPartialOf<F, S, S>, A> =
    IndexedStateT.pure(FF(), a)

  override fun <A, B> Kind<IndexedStateTPartialOf<F, S, S>, A>.ap(ff: Kind<IndexedStateTPartialOf<F, S, S>, (A) -> B>): Kind<IndexedStateTPartialOf<F, S, S>, B> =
    this.fix().ap(FF(), ff)


  override fun <A, B> Kind<IndexedStateTPartialOf<F, S, S>, A>.map(f: (A) -> B): Kind<IndexedStateTPartialOf<F, S, S>, B> =
    this.fix().map(FF(), f)

}

@instance(IndexedStateT::class)
interface IndexedStateTMonadInstance<F, S> : IndexedStateTFunctorInstance<F, S>, Monad<IndexedStateTPartialOf<F, S, S>> {

  override fun FF(): Monad<F>

  override fun <A, B> Kind<IndexedStateTPartialOf<F, S, S>, A>.flatMap(f: (A) -> Kind<IndexedStateTPartialOf<F, S, S>, B>): Kind<IndexedStateTPartialOf<F, S, S>, B> =
    this.fix().flatMap(FF(), f)

  override fun <A, B> tailRecM(a: A, f: (A) -> Kind<IndexedStateTPartialOf<F, S, S>, Either<A, B>>): Kind<IndexedStateTPartialOf<F, S, S>, B> =
    IndexedStateT.tailRecM(FF(), a, f)

  override fun <A, B> Kind<IndexedStateTPartialOf<F, S, S>, A>.map(f: (A) -> B): Kind<IndexedStateTPartialOf<F, S, S>, B> =
    this.fix().map(FF(), f)

  override fun <A> just(a: A): Kind<IndexedStateTPartialOf<F, S, S>, A> =
    IndexedStateT.pure(FF(), a)

}

@instance(IndexedStateT::class)
interface IndexedStateTSemigroupKInstance<F, SA, SB> : SemigroupK<IndexedStateTPartialOf<F, SA, SB>> {

  fun MF(): Monad<F>

  fun SS(): SemigroupK<F>

  override fun <A> Kind<IndexedStateTPartialOf<F, SA, SB>, A>.combineK(y: Kind<IndexedStateTPartialOf<F, SA, SB>, A>): Kind<IndexedStateTPartialOf<F, SA, SB>, A> =
      this.fix().combineK(y, MF(), SS())

}

@instance(IndexedStateT::class)
interface IndexedStateTApplicativeErrorInstance<F, S, E> : IndexedStateTApplicativeInstance<F, S>, ApplicativeError<IndexedStateTPartialOf<F, S, S>, E> {
  override fun FF(): MonadError<F, E>

  override fun <A> raiseError(e: E): Kind<IndexedStateTPartialOf<F, S, S>, A> = IndexedStateT.lift(FF(), FF().raiseError(e))

  override fun <A> Kind<StateTPartialOf<F, S>, A>.handleErrorWith(f: (E) -> Kind<IndexedStateTPartialOf<F, S, S>, A>): StateT<F, S, A> =
    IndexedStateT(FF().just({ s -> FF().run { runM(FF(), s).handleErrorWith { e -> f(e).runM(FF(), s) } } }))

}

interface IndexedStateTMonadErrorInstance<F, S, E> : IndexedStateTApplicativeErrorInstance<F, S, E>, IndexedStateTMonadInstance<F, S>, MonadError<IndexedStateTPartialOf<F, S, S>, E> {

  override fun FF(): MonadError<F, E>

  override fun <A> just(a: A): Kind<IndexedStateTPartialOf<F, S, S>, A> = IndexedStateT.pure(FF(), a)

  override fun <A, B> Kind<IndexedStateTPartialOf<F, S, S>, A>.ap(ff: Kind<IndexedStateTPartialOf<F, S, S>, (A) -> B>): Kind<IndexedStateTPartialOf<F, S, S>, B> =
    this.fix().ap(FF(), ff)

  override fun <A, B> Kind<IndexedStateTPartialOf<F, S, S>, A>.map(f: (A) -> B): Kind<IndexedStateTPartialOf<F, S, S>, B> =
    this.fix().map(FF(), f)

}

fun <F, S, E> arrow.data.IndexedStateT.Companion.monadError(FF: arrow.typeclasses.MonadError<F, E>, @Suppress("UNUSED_PARAMETER") dummy: kotlin.Unit = kotlin.Unit): IndexedStateTMonadErrorInstance<F, S, E> =
  object : IndexedStateTMonadErrorInstance<F, S, E> {
    override fun FF(): arrow.typeclasses.MonadError<F, E> = FF
  }

//
///**
// * Alias for[StateT.Companion.applicative]
// */
//fun <S> StateApi.applicative(): Applicative<StateTPartialOf<ForId, S>> = StateT.applicative<ForId, S>(Id.monad())
//
///**
// * Alias for [StateT.Companion.functor]
// */
//fun <S> StateApi.functor(): Functor<StateTPartialOf<ForId, S>> = StateT.functor<ForId, S>(Id.monad())
//
///**
// * Alias for [StateT.Companion.monad]
// */
//fun <S> StateApi.monad(): Monad<StateTPartialOf<ForId, S>> = StateT.monad<ForId, S>(Id.monad())
//
//class StateTContext<F, S, E>(val ME: MonadError<F, E>) : StateTMonadErrorInstance<F, S, E> {
//  override fun FF(): MonadError<F, E> = ME
//}
//
//class StateTContextPartiallyApplied<F, S, E>(val ME: MonadError<F, E>) {
//  infix fun <A> extensions(f: StateTContext<F, S, E>.() -> A): A =
//    f(StateTContext(ME))
//}
//
//fun <F, S, E> ForStateT(ME: MonadError<F, E>): StateTContextPartiallyApplied<F, S, E> =
//  StateTContextPartiallyApplied(ME)
//
//class StateTMonadContext<S> : StateTMonadInstance<ForId, S> {
//  override fun FF(): Monad<ForId> = Id.monad()
//}
//
//class StateContextPartiallyApplied<S>() {
//  infix fun <A> extensions(f: StateTMonadContext<S>.() -> A): A =
//    f(StateTMonadContext())
//}
//
//fun <S> ForState(): StateContextPartiallyApplied<S> =
//  StateContextPartiallyApplied()
