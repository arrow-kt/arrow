package arrow.dagger.effects.instances.rx2

import arrow.effects.*
import arrow.typeclasses.*
import dagger.Module
import dagger.Provides

@Module
class FlowableKWInstances {

    @Provides
    fun flowableKWFunctor(): Functor<FlowableKWHK> = FlowableKW.functor()

    @Provides
    fun flowableKWApplicative(): Applicative<FlowableKWHK> = FlowableKW.applicative()

    @Provides
    fun flowableKWApplicativeError(): ApplicativeError<FlowableKWHK, Throwable> = FlowableKW.applicativeError()

    @Provides
    fun flowableKWMonad(): Monad<FlowableKWHK> = FlowableKW.monad()

    @Provides
    fun flowableKWMonadError(): MonadError<FlowableKWHK, Throwable> = FlowableKW.monadError()

    @Provides
    fun flowableKWMonadSuspend(): MonadSuspend<FlowableKWHK> = FlowableKW.monadSuspend()

    @Provides
    fun flowableKWAsync(): Async<FlowableKWHK> = FlowableKW.async()

    @Provides
    fun flowableKWEffect(): Effect<FlowableKWHK> = FlowableKW.effect()

}