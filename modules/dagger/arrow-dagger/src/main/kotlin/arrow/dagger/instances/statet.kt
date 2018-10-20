package arrow.dagger.instances

import arrow.data.IndexedStateTPartialOf
import arrow.instances.*
import arrow.typeclasses.*
import dagger.Module
import dagger.Provides
import javax.inject.Inject

@Module
abstract class StateTInstances<F, S> {

  @Provides
  fun stateTFunctor(ev: DaggerIndexedStateTFunctorInstance<F, S>): Functor<IndexedStateTPartialOf<F, S, S>> = ev

  @Provides
  fun stateTApplicative(ev: DaggerIndexedStateTApplicativeInstance<F, S>): Applicative<IndexedStateTPartialOf<F, S, S>> = ev

  @Provides
  fun stateTMonad(ev: DaggerIndexedStateTMonadInstance<F, S>): Monad<IndexedStateTPartialOf<F, S, S>> = ev

  @Provides
  fun stateTApplicativeError(ev: DaggerIndexedStateTMonadErrorInstance<F, S>): ApplicativeError<IndexedStateTPartialOf<F, S, S>, S> = ev

  @Provides
  fun stateTMonadError(ev: DaggerIndexedStateTMonadErrorInstance<F, S>): MonadError<IndexedStateTPartialOf<F, S, S>, S> = ev
}

class DaggerIndexedStateTFunctorInstance<F, L> @Inject constructor(val FF: Functor<F>) : IndexedStateTFunctorInstance<F, L> {
  override fun FF(): Functor<F> = FF
}

class DaggerIndexedStateTApplicativeInstance<F, L> @Inject constructor(val MF: Monad<F>) : IndexedStateTApplicativeInstance<F, L> {
  override fun FF(): Monad<F> = MF
}

class DaggerIndexedStateTMonadInstance<F, L> @Inject constructor(val MF: Monad<F>) : IndexedStateTMonadInstance<F, L> {
  override fun FF(): Monad<F> = MF
}

class DaggerIndexedStateTMonadErrorInstance<F, L> @Inject constructor(val MF: MonadError<F, L>) : IndexedStateTMonadErrorInstance<F, L, L> {
  override fun FF(): MonadError<F, L> = MF
}