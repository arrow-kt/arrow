package arrow.dagger.instances

import arrow.data.*
import arrow.typeclasses.*
import dagger.Module
import dagger.Provides

@Module
class NonEmptyListInstances {

    @Provides
    fun nonEmptyListFunctor(): Functor<NonEmptyListHK> = NonEmptyList.functor()

    @Provides
    fun nonEmptyListApplicative(): Applicative<NonEmptyListHK> = NonEmptyList.applicative()

    @Provides
    fun nonEmptyListMonad(): Monad<NonEmptyListHK> = NonEmptyList.monad()

    @Provides
    fun nonEmptyListFoldable(): Foldable<NonEmptyListHK> = NonEmptyList.foldable()

    @Provides
    fun nonEmptyListTraverse(): Traverse<NonEmptyListHK> = NonEmptyList.traverse()

    @Provides
    fun nonEmptyListSemigroupK(): SemigroupK<NonEmptyListHK> = NonEmptyList.semigroupK()

    @Provides
    fun nonEmptyListComonad(): Comonad<NonEmptyListHK> = NonEmptyList.comonad()

    @Provides
    fun nonEmptyListBimonad(): Bimonad<NonEmptyListHK> = NonEmptyList.bimonad()

}