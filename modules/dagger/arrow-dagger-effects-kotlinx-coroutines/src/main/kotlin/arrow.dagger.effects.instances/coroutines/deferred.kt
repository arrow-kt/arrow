package arrow.dagger.effects.instances.coroutines

import arrow.effects.*
import arrow.effects.syntax.deferredk.applicative.applicative
import arrow.effects.syntax.deferredk.applicativeError.applicativeError
import arrow.effects.syntax.deferredk.async.async
import arrow.effects.syntax.deferredk.effect.effect
import arrow.effects.syntax.deferredk.functor.functor
import arrow.effects.syntax.deferredk.monad.monad
import arrow.effects.syntax.deferredk.monadDefer.monadDefer
import arrow.effects.syntax.deferredk.monadError.monadError
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