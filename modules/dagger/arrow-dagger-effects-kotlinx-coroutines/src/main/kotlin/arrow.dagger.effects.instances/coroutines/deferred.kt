package arrow.dagger.effects.instances.coroutines

import arrow.effects.*
import arrow.typeclasses.*
import dagger.Module
import dagger.Provides

@Module
class DeferredKWInstances {

    @Provides
    fun deferredKWFunctor(): Functor<DeferredKWHK> = DeferredKW.functor()

    @Provides
    fun deferredKWApplicative(): Applicative<DeferredKWHK> = DeferredKW.applicative()

    @Provides
    fun deferredKWApplicativeError(): ApplicativeError<DeferredKWHK, Throwable> = DeferredKW.applicativeError()

    @Provides
    fun deferredKWMonad(): Monad<DeferredKWHK> = DeferredKW.monad()

    @Provides
    fun deferredKWMonadError(): MonadError<DeferredKWHK, Throwable> = DeferredKW.monadError()

    @Provides
    fun deferredKWMonadSuspend(): MonadSuspend<DeferredKWHK> = DeferredKW.monadSuspend()

    @Provides
    fun deferredKWAsync(): Async<DeferredKWHK> = DeferredKW.async()

    @Provides
    fun deferredKWEffect(): Effect<DeferredKWHK> = DeferredKW.effect()

}