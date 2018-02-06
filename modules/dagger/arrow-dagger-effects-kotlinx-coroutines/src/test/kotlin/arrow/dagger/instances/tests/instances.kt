package arrow.dagger.instances.tests

import arrow.dagger.effects.instances.coroutines.ArrowEffectsCoroutinesInstances
import arrow.effects.Async
import arrow.effects.DeferredKWHK
import arrow.effects.Effect
import arrow.effects.MonadSuspend
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
    ArrowEffectsCoroutinesInstances::class
])
interface Runtime {
    fun deferredKWFunctor(): Functor<DeferredKWHK>
    fun deferredKWApplicative(): Applicative<DeferredKWHK>
    fun deferredKWApplicativeError(): ApplicativeError<DeferredKWHK, Throwable>
    fun deferredKWMonad(): Monad<DeferredKWHK>
    fun deferredKWMonadError(): MonadError<DeferredKWHK, Throwable>
    fun deferredKWMonadSuspend(): MonadSuspend<DeferredKWHK>
    fun deferredKWAsync(): Async<DeferredKWHK>
    fun deferredKWEffect(): Effect<DeferredKWHK>
}

object Arrow {
    val instances = DaggerRuntime.builder().build()
}



