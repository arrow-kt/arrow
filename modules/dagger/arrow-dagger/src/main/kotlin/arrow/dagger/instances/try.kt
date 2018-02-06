package arrow.dagger.instances

import arrow.data.*
import arrow.typeclasses.*
import dagger.Module
import dagger.Provides

@Module
class TryInstances {

    @Provides
    fun tryFunctor(): Functor<TryHK> = Try.functor()

    @Provides
    fun tryApplicative(): Applicative<TryHK> = Try.applicative()

    @Provides
    fun tryMonad(): Monad<TryHK> = Try.monad()

    @Provides
    fun tryMonadError(): MonadError<TryHK, Throwable> = Try.monadError()

    @Provides
    fun tryFoldable(): Foldable<TryHK> = Try.foldable()

    @Provides
    fun tryTraverse(): Traverse<TryHK> = Try.traverse()

}