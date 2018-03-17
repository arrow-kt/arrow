package arrow.dagger.instances.tests

import arrow.dagger.effects.instances.coroutines.ArrowEffectsCoroutinesInstances
import arrow.effects.Async
import arrow.effects.Effect
import arrow.effects.ForDeferredK
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
    fun deferredKFunctor(): Functor<ForDeferredK>
    fun deferredKApplicative(): Applicative<ForDeferredK>
    fun deferredKApplicativeError(): ApplicativeError<ForDeferredK, Throwable>
    fun deferredKMonad(): Monad<ForDeferredK>
    fun deferredKMonadError(): MonadError<ForDeferredK, Throwable>
    fun deferredKMonadSuspend(): MonadSuspend<ForDeferredK, Throwable>
    fun deferredKAsync(): Async<ForDeferredK, Throwable>
    fun deferredKEffect(): Effect<ForDeferredK, Throwable>
}

object Arrow {
    val instances = DaggerRuntime.builder().build()
}



