package arrow.dagger.effects.extensions

import arrow.effects.*
import arrow.effects.extensions.IOMonoid
import arrow.effects.extensions.IOSemigroup
import arrow.effects.extensions.io.applicative.applicative
import arrow.effects.extensions.io.applicativeError.applicativeError
import arrow.effects.extensions.io.async.async
import arrow.effects.extensions.io.bracket.bracket
import arrow.effects.extensions.io.effect.effect
import arrow.effects.extensions.io.functor.functor
import arrow.effects.extensions.io.monad.monad
import arrow.effects.extensions.io.monadDefer.monadDefer
import arrow.effects.extensions.io.monadError.monadError
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

class DaggerIOSemigroup<A> @Inject constructor(val monoidA: Monoid<A>) : IOSemigroup<A> {
  override fun SG(): Semigroup<A> = monoidA
}

class DaggerIOMonoid<A> @Inject constructor(val monoidA: Monoid<A>) : IOMonoid<A> {
  override fun SM(): Monoid<A> = monoidA
}