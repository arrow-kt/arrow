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
    fun observableKFunctor(): Functor<ForObservableK>
    fun observableKApplicative(): Applicative<ForObservableK>
    fun observableKApplicativeError(): ApplicativeError<ForObservableK, Throwable>
    fun observableKMonad(): Monad<ForObservableK>
    fun observableKMonadError(): MonadError<ForObservableK, Throwable>
    fun observableKMonadSuspend(): MonadSuspend<ForObservableK, Throwable>
    fun observableKAsync(): Async<ForObservableK, Throwable>
    fun observableKEffect(): Effect<ForObservableK, Throwable>
    fun flowableKFunctor(): Functor<ForFlowableK>
    fun flowableKApplicative(): Applicative<ForFlowableK>
    fun flowableKApplicativeError(): ApplicativeError<ForFlowableK, Throwable>
    fun flowableKMonad(): Monad<ForFlowableK>
    fun flowableKMonadError(): MonadError<ForFlowableK, Throwable>
    fun flowableKMonadSuspend(): MonadSuspend<ForFlowableK, Throwable>
    fun flowableKAsync(): Async<ForFlowableK, Throwable>
    fun flowableKEffect(): Effect<ForFlowableK, Throwable>
}

object Arrow {
    val instances = DaggerRuntime.builder().build()
}



