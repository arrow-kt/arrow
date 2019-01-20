package arrow.dagger.extensions

import arrow.data.StateTPartialOf
import arrow.data.extensions.*
import arrow.typeclasses.*
import dagger.Module
import dagger.Provides
import javax.inject.Inject

@Module
abstract class StateTInstances<F, S> {

  @Provides
  fun stateTFunctor(ev: DaggerStateTFunctor<F, S>): Functor<StateTPartialOf<F, S>> = ev

  @Provides
  fun stateTApplicative(ev: DaggerStateTApplicative<F, S>): Applicative<StateTPartialOf<F, S>> = ev

  @Provides
  fun stateTMonad(ev: DaggerStateTMonad<F, S>): Monad<StateTPartialOf<F, S>> = ev

  @Provides
  fun stateTApplicativeError(ev: DaggerStateTMonadError<F, S>): ApplicativeError<StateTPartialOf<F, S>, S> = ev

  @Provides
  fun stateTMonadError(ev: DaggerStateTMonadError<F, S>): MonadError<StateTPartialOf<F, S>, S> = ev
}

class DaggerStateTFunctor<F, L> @Inject constructor(val FF: Functor<F>) : StateTFunctor<F, L> {
  override fun FF(): Functor<F> = FF
}

class DaggerStateTApplicative<F, L> @Inject constructor(val MF: Monad<F>) : StateTApplicative<F, L> {
  override fun FF(): Monad<F> = MF
  override fun MF(): Monad<F> = MF
}

class DaggerStateTMonad<F, L> @Inject constructor(val MF: Monad<F>) : StateTMonad<F, L> {
  override fun FF(): Monad<F> = MF
  override fun MF(): Monad<F> = MF
}

class DaggerStateTMonadError<F, L> @Inject constructor(val MF: MonadError<F, L>) : StateTMonadError<F, L, L> {
  override fun FF(): MonadError<F, L> = MF
  override fun ME(): MonadError<F, L> = MF
}