package arrow.dagger.instances

import arrow.core.*
import arrow.instances.option.applicative.applicative
import arrow.instances.option.foldable.foldable
import arrow.instances.option.functor.functor
import arrow.instances.option.monad.monad
import arrow.instances.option.monadError.monadError
import arrow.instances.option.traverse.traverse
import arrow.typeclasses.*
import dagger.Module
import dagger.Provides

@Module
class OptionInstances {

  @Provides
  fun optionFunctor(): Functor<ForOption> = Option.functor()

  @Provides
  fun optionApplicative(): Applicative<ForOption> = Option.applicative()

  @Provides
  fun optionMonad(): Monad<ForOption> = Option.monad()

  @Provides
  fun optionMonadError(): MonadError<ForOption, Unit> = Option.monadError()

  @Provides
  fun optionFoldable(): Foldable<ForOption> = Option.foldable()

  @Provides
  fun optionTraverse(): Traverse<ForOption> = Option.traverse()

}
