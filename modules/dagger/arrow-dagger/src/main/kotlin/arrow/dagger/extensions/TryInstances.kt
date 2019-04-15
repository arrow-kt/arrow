package arrow.dagger.extensions

import arrow.core.ForTry
import arrow.core.Try
import arrow.core.extensions.`try`.applicative.applicative
import arrow.core.extensions.`try`.foldable.foldable
import arrow.core.extensions.`try`.functor.functor
import arrow.core.extensions.`try`.monad.monad
import arrow.core.extensions.`try`.monadError.monadError
import arrow.core.extensions.`try`.traverse.traverse
import arrow.typeclasses.Applicative
import arrow.typeclasses.Foldable
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
import arrow.typeclasses.MonadError
import arrow.typeclasses.Traverse
import dagger.Module
import dagger.Provides

@Module
class TryInstances {

  @Provides
  fun tryFunctor(): Functor<ForTry> = Try.functor()

  @Provides
  fun tryApplicative(): Applicative<ForTry> = Try.applicative()

  @Provides
  fun tryMonad(): Monad<ForTry> = Try.monad()

  @Provides
  fun tryMonadError(): MonadError<ForTry, Throwable> = Try.monadError()

  @Provides
  fun tryFoldable(): Foldable<ForTry> = Try.foldable()

  @Provides
  fun tryTraverse(): Traverse<ForTry> = Try.traverse()
}
