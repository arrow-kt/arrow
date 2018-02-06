package arrow.dagger.effects.instances.tests

import arrow.dagger.effects.instances.rx2.ArrowEffectsRx2Instances
import arrow.effects.*
import arrow.typeclasses.*
import dagger.Component
import javax.inject.Singleton

/**
 * If the component below compiles it means the `ArrowEffectsCoroutinesInstances` was successful resolving
 * all the declared below instances at compile time.
 * and need explicit evidence of a @Module such as `LocalStateTInstances`
 */
@Singleton
@Component(modules = [
    ArrowEffectsRx2Instances::class
])
interface Runtime {
    fun observableKWFunctor(): Functor<ObservableKWHK>
    fun observableKWApplicative(): Applicative<ObservableKWHK>
    fun observableKWApplicativeError(): ApplicativeError<ObservableKWHK, Throwable>
    fun observableKWMonad(): Monad<ObservableKWHK>
    fun observableKWMonadError(): MonadError<ObservableKWHK, Throwable>
    fun observableKWMonadSuspend(): MonadSuspend<ObservableKWHK>
    fun observableKWAsync(): Async<ObservableKWHK>
    fun observableKWEffect(): Effect<ObservableKWHK>
    fun flowableKWFunctor(): Functor<FlowableKWHK>
    fun flowableKWApplicative(): Applicative<FlowableKWHK>
    fun flowableKWApplicativeError(): ApplicativeError<FlowableKWHK, Throwable>
    fun flowableKWMonad(): Monad<FlowableKWHK>
    fun flowableKWMonadError(): MonadError<FlowableKWHK, Throwable>
    fun flowableKWMonadSuspend(): MonadSuspend<FlowableKWHK>
    fun flowableKWAsync(): Async<FlowableKWHK>
    fun flowableKWEffect(): Effect<FlowableKWHK>
}

object Arrow {
    val instances = DaggerRuntime.builder().build()
}



