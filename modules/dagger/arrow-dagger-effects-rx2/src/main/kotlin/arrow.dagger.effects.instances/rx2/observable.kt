package arrow.dagger.effects.instances.rx2

import arrow.effects.*
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
    fun observableKMonadSuspend(): MonadSuspend<ForObservableK> = ObservableK.monadSuspend()

    @Provides
    fun observableKAsync(): Async<ForObservableK> = ObservableK.async()

    @Provides
    fun observableKEffect(): Effect<ForObservableK> = ObservableK.effect()

}