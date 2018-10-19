package arrow.dagger.effects.instances.rx2

import arrow.effects.*
import arrow.effects.observablek.applicative.applicative
import arrow.effects.observablek.applicativeError.applicativeError
import arrow.effects.observablek.async.async
import arrow.effects.observablek.effect.effect
import arrow.effects.observablek.functor.functor
import arrow.effects.observablek.monad.monad
import arrow.effects.observablek.monadDefer.monadDefer
import arrow.effects.observablek.monadError.monadError
import arrow.effects.typeclasses.Async
import arrow.effects.typeclasses.Effect
import arrow.effects.typeclasses.MonadDefer
import arrow.typeclasses.*
import dagger.Module
import dagger.Provides

@Module
class ObservableKInstances {

  @Provides
  fun observableKFunctor(): Functor<ForObservableK> = ObservableK.functor()

  @Provides
  fun observableKApplicative(): Applicative<ForObservableK> = ObservableK.applicative()

  @Provides
  fun observableKApplicativeError(): ApplicativeError<ForObservableK, Throwable> = ObservableK.applicativeError()

  @Provides
  fun observableKMonad(): Monad<ForObservableK> = ObservableK.monad()

  @Provides
  fun observableKMonadError(): MonadError<ForObservableK, Throwable> = ObservableK.monadError()

  @Provides
  fun observableKMonadSuspend(): MonadDefer<ForObservableK> = ObservableK.monadDefer()

  @Provides
  fun observableKAsync(): Async<ForObservableK> = ObservableK.async()

  @Provides
  fun observableKEffect(): Effect<ForObservableK> = ObservableK.effect()

}