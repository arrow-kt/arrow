package arrow.dagger.instances

import arrow.data.*
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