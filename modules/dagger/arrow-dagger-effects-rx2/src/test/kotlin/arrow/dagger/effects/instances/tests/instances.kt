package arrow.dagger.effects.instances.tests

import arrow.dagger.effects.instances.rx2.ArrowEffectsRx2Instances
import arrow.effects.ForFlowableK
import arrow.effects.ForObservableK
import arrow.effects.typeclasses.Async
import arrow.effects.typeclasses.Effect
import arrow.effects.typeclasses.MonadSuspend
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
    fun observableKWFunctor(): Functor<ForObservableK>
    fun observableKWApplicative(): Applicative<ForObservableK>
    fun observableKWApplicativeError(): ApplicativeError<ForObservableK, Throwable>
    fun observableKWMonad(): Monad<ForObservableK>
    fun observableKWMonadError(): MonadError<ForObservableK, Throwable>
    fun observableKWMonadSuspend(): MonadSuspend<ForObservableK>
    fun observableKWAsync(): Async<ForObservableK>
    fun observableKWEffect(): Effect<ForObservableK>
    fun flowableKWFunctor(): Functor<ForFlowableK>
    fun flowableKWApplicative(): Applicative<ForFlowableK>
    fun flowableKWApplicativeError(): ApplicativeError<ForFlowableK, Throwable>
    fun flowableKWMonad(): Monad<ForFlowableK>
    fun flowableKWMonadError(): MonadError<ForFlowableK, Throwable>
    fun flowableKWMonadSuspend(): MonadSuspend<ForFlowableK>
    fun flowableKWAsync(): Async<ForFlowableK>
    fun flowableKWEffect(): Effect<ForFlowableK>
}

object Arrow {
    val instances = DaggerRuntime.builder().build()
}



