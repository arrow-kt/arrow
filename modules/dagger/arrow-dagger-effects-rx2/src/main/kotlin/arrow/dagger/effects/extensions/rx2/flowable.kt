package arrow.dagger.effects.extensions.rx2

import arrow.effects.rx2.FlowableK
import arrow.effects.rx2.ForFlowableK
import arrow.effects.typeclasses.Async
import arrow.effects.typeclasses.Effect
import arrow.effects.typeclasses.MonadDefer
import arrow.effects.rx2.extensions.flowablek.applicative.applicative
import arrow.effects.rx2.extensions.flowablek.applicativeError.applicativeError
import arrow.effects.rx2.extensions.flowablek.async.async
import arrow.effects.rx2.extensions.flowablek.effect.effect
import arrow.effects.rx2.extensions.flowablek.functor.functor
import arrow.effects.rx2.extensions.flowablek.monad.monad
import arrow.effects.rx2.extensions.flowablek.monadDefer.monadDefer
import arrow.effects.rx2.extensions.flowablek.monadError.monadError
import arrow.typeclasses.*
import dagger.Module
import dagger.Provides

@Module
class FlowableKInstances {

  @Provides
  fun flowableKFunctor(): Functor<ForFlowableK> = FlowableK.functor()

  @Provides
  fun flowableKApplicative(): Applicative<ForFlowableK> = FlowableK.applicative()

  @Provides
  fun flowableKApplicativeError(): ApplicativeError<ForFlowableK, Throwable> = FlowableK.applicativeError()

  @Provides
  fun flowableKMonad(): Monad<ForFlowableK> = FlowableK.monad()

  @Provides
  fun flowableKMonadError(): MonadError<ForFlowableK, Throwable> = FlowableK.monadError()

  @Provides
  fun flowableKMonadSuspend(): MonadDefer<ForFlowableK> = FlowableK.monadDefer()

  @Provides
  fun flowableKAsync(): Async<ForFlowableK> = FlowableK.async()

  @Provides
  fun flowableKEffect(): Effect<ForFlowableK> = FlowableK.effect()

}