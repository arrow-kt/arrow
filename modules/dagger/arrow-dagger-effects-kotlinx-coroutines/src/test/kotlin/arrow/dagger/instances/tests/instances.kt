package arrow.dagger.instances.tests

import arrow.dagger.effects.instances.coroutines.ArrowEffectsCoroutinesInstances
import arrow.effects.typeclasses.Async
import arrow.effects.ForDeferredK
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
    ArrowEffectsCoroutinesInstances::class
])
interface Runtime {
    fun deferredKWFunctor(): Functor<ForDeferredK>
    fun deferredKWApplicative(): Applicative<ForDeferredK>
    fun deferredKWApplicativeError(): ApplicativeError<ForDeferredK, Throwable>
    fun deferredKWMonad(): Monad<ForDeferredK>
    fun deferredKWMonadError(): MonadError<ForDeferredK, Throwable>
    fun deferredKWMonadSuspend(): MonadSuspend<ForDeferredK>
    fun deferredKWAsync(): Async<ForDeferredK>
    fun deferredKWEffect(): Effect<ForDeferredK>
}

object Arrow {
    val instances = DaggerRuntime.builder().build()
}



