package arrow.dagger.effects.instances

import arrow.effects.*
import arrow.effects.instances.IOMonoidInstance
import arrow.effects.instances.IOSemigroupInstance
import arrow.effects.instances.io.applicative.applicative
import arrow.effects.instances.io.applicativeError.applicativeError
import arrow.effects.instances.io.async.async
import arrow.effects.instances.io.bracket.bracket
import arrow.effects.instances.io.effect.effect
import arrow.effects.instances.io.functor.functor
import arrow.effects.instances.io.monad.monad
import arrow.effects.instances.io.monadDefer.monadDefer
import arrow.effects.instances.io.monadError.monadError
import arrow.effects.typeclasses.Async
import arrow.effects.typeclasses.Bracket
import arrow.effects.typeclasses.Effect
import arrow.effects.typeclasses.MonadDefer
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
  fun ioMonadSuspend(): MonadDefer<ForIO> = IO.monadDefer()

  @Provides
  fun ioBracket(): Bracket<ForIO, Throwable> = IO.bracket()

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