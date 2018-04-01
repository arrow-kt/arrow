package arrow.dagger.effects.instances

import arrow.effects.*
import arrow.effects.typeclasses.Async
import arrow.effects.typeclasses.Effect
import arrow.effects.typeclasses.MonadSuspend
import arrow.typeclasses.*
import dagger.Module
import dagger.Provides
import javax.inject.Inject

@Module
class IOInstances {

  @Provides
  fun ioFunctor(): Functor<ForIO> = IO.functor()

  @Provides
  fun ioApplicative(): Applicative<ForIO> = IO.applicative()

  @Provides
  fun ioApplicativeError(): ApplicativeError<ForIO, Throwable> = IO.applicativeError()

  @Provides
  fun ioMonad(): Monad<ForIO> = IO.monad()

  @Provides
  fun ioMonadError(): MonadError<ForIO, Throwable> = IO.monadError()

  @Provides
  fun ioMonadSuspend(): MonadSuspend<ForIO> = IO.monadSuspend()

  @Provides
  fun ioAsync(): Async<ForIO> = IO.async()

  @Provides
  fun ioEffect(): Effect<ForIO> = IO.effect()

}

class DaggerIOSemigroupInstance<A> @Inject constructor(val monoidA: Monoid<A>) : IOSemigroupInstance<A> {
  override fun SG(): Semigroup<A> = monoidA
}

class DaggerIOMonoidInstance<A> @Inject constructor(val monoidA: Monoid<A>) : IOMonoidInstance<A> {
  override fun SM(): Monoid<A> = monoidA
}