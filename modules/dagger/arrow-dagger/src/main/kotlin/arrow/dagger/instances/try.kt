package arrow.dagger.instances

import arrow.core.*
import arrow.instances.syntax.`try`.applicative.applicative
import arrow.instances.syntax.`try`.foldable.foldable
import arrow.instances.syntax.`try`.functor.functor
import arrow.instances.syntax.`try`.monad.monad
import arrow.instances.syntax.`try`.monadError.monadError
import arrow.instances.syntax.`try`.traverse.traverse
import arrow.typeclasses.*
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