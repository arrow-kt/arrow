package arrow.dagger.effects.instances.coroutines

import arrow.effects.*
import arrow.effects.typeclasses.Async
import arrow.effects.typeclasses.Effect
import arrow.effects.typeclasses.MonadSuspend
import arrow.typeclasses.*
import dagger.Module
import dagger.Provides

@Module
class DeferredKInstances {

  @Provides
  fun deferredKFunctor(): Functor<ForDeferredK> = DeferredK.functor()

  @Provides
  fun deferredKApplicative(): Applicative<ForDeferredK> = DeferredK.applicative()

  @Provides
  fun deferredKApplicativeError(): ApplicativeError<ForDeferredK, Throwable> = DeferredK.applicativeError()

  @Provides
  fun deferredKMonad(): Monad<ForDeferredK> = DeferredK.monad()

  @Provides
  fun deferredKMonadError(): MonadError<ForDeferredK, Throwable> = DeferredK.monadError()

  @Provides
  fun deferredKMonadSuspend(): MonadSuspend<ForDeferredK> = DeferredK.monadSuspend()

  @Provides
  fun deferredKAsync(): Async<ForDeferredK> = DeferredK.async()

  @Provides
  fun deferredKEffect(): Effect<ForDeferredK> = DeferredK.effect()

}