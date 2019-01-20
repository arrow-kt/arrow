package arrow.dagger.effects.extensions.coroutines

import arrow.effects.coroutines.DeferredK
import arrow.effects.coroutines.ForDeferredK
import arrow.effects.coroutines.extensions.deferredk.applicative.applicative
import arrow.effects.coroutines.extensions.deferredk.applicativeError.applicativeError
import arrow.effects.coroutines.extensions.deferredk.async.async
import arrow.effects.coroutines.extensions.deferredk.effect.effect
import arrow.effects.coroutines.extensions.deferredk.functor.functor
import arrow.effects.coroutines.extensions.deferredk.monad.monad
import arrow.effects.coroutines.extensions.deferredk.monadDefer.monadDefer
import arrow.effects.coroutines.extensions.deferredk.monadError.monadError
import arrow.effects.typeclasses.Async
import arrow.effects.typeclasses.Effect
import arrow.effects.typeclasses.MonadDefer
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
  fun deferredKMonadSuspend(): MonadDefer<ForDeferredK> = DeferredK.monadDefer()

  @Provides
  fun deferredKAsync(): Async<ForDeferredK> = DeferredK.async()

  @Provides
  fun deferredKEffect(): Effect<ForDeferredK> = DeferredK.effect()

}