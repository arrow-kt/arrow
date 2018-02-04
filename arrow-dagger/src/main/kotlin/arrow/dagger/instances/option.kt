package arrow.dagger.instances

import arrow.core.*
import arrow.typeclasses.*
import dagger.Module
import dagger.Provides

@Module
class OptionInstances {

    @Provides
    fun optionFunctor(): Functor<OptionHK> = Option.functor()

    @Provides
    fun optionApplicative(): Applicative<OptionHK> = Option.applicative()

    @Provides
    fun optionMonad(): Monad<OptionHK> = Option.monad()

    @Provides
    fun optionMonadError(): MonadError<OptionHK, Unit> = Option.monadError()

    @Provides
    fun optionFoldable(): Foldable<OptionHK> = Option.foldable()

    @Provides
    fun optionTraverse(): Traverse<OptionHK> = Option.traverse()

}
