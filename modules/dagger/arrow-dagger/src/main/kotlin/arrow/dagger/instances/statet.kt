package arrow.dagger.instances

import arrow.data.StateTPartialOf
import arrow.instances.StateTApplicativeInstance
import arrow.instances.StateTFunctorInstance
import arrow.instances.StateTMonadErrorInstance
import arrow.instances.StateTMonadInstance
import arrow.typeclasses.*
import dagger.Module
import dagger.Provides
import javax.inject.Inject

@Module
abstract class StateTInstances<F, S> {

  @Provides
  fun stateTFunctor(ev: DaggerStateTFunctorInstance<F, S>): Functor<StateTPartialOf<F, S>> = ev

  @Provides
  fun stateTApplicative(ev: DaggerStateTApplicativeInstance<F, S>): Applicative<StateTPartialOf<F, S>> = ev

  @Provides
  fun stateTMonad(ev: DaggerStateTMonadInstance<F, S>): Monad<StateTPartialOf<F, S>> = ev

  @Provides
  fun stateTApplicativeError(ev: DaggerStateTMonadErrorInstance<F, S>): ApplicativeError<StateTPartialOf<F, S>, S> = ev

  @Provides
  fun stateTMonadError(ev: DaggerStateTMonadErrorInstance<F, S>): MonadError<StateTPartialOf<F, S>, S> = ev
}

class DaggerStateTFunctorInstance<F, L> @Inject constructor(val FF: Functor<F>) : StateTFunctorInstance<F, L> {
  override fun FF(): Functor<F> = FF
}

class DaggerStateTApplicativeInstance<F, L> @Inject constructor(val MF: Monad<F>) : StateTApplicativeInstance<F, L> {
  override fun FF(): Monad<F> = MF
  override fun MF(): Monad<F> = MF
}

class DaggerStateTMonadInstance<F, L> @Inject constructor(val MF: Monad<F>) : StateTMonadInstance<F, L> {
  override fun FF(): Monad<F> = MF
  override fun MF(): Monad<F> = MF
}

class DaggerStateTMonadErrorInstance<F, L> @Inject constructor(val MF: MonadError<F, L>) : StateTMonadErrorInstance<F, L, L> {
  override fun FF(): MonadError<F, L> = MF
  override fun ME(): MonadError<F, L> = MF
}