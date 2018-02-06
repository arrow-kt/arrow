package arrow.dagger.effects.instances.rx2

import arrow.effects.*
import arrow.typeclasses.*
import dagger.Module
import dagger.Provides

@Module
class ObservableKWInstances {

    @Provides
    fun observableKWFunctor(): Functor<ObservableKWHK> = ObservableKW.functor()

    @Provides
    fun observableKWApplicative(): Applicative<ObservableKWHK> = ObservableKW.applicative()

    @Provides
    fun observableKWApplicativeError(): ApplicativeError<ObservableKWHK, Throwable> = ObservableKW.applicativeError()

    @Provides
    fun observableKWMonad(): Monad<ObservableKWHK> = ObservableKW.monad()

    @Provides
    fun observableKWMonadError(): MonadError<ObservableKWHK, Throwable> = ObservableKW.monadError()

    @Provides
    fun observableKWMonadSuspend(): MonadSuspend<ObservableKWHK> = ObservableKW.monadSuspend()

    @Provides
    fun observableKWAsync(): Async<ObservableKWHK> = ObservableKW.async()

    @Provides
    fun observableKWEffect(): Effect<ObservableKWHK> = ObservableKW.effect()

}