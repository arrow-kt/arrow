package arrow.dagger.instances

import arrow.data.*
import arrow.typeclasses.*
import dagger.Module
import dagger.Provides

@Module
class ListKWInstances {

    @Provides
    fun listKWFunctor(): Functor<ListKWHK> = ListKW.functor()

    @Provides
    fun listKWApplicative(): Applicative<ListKWHK> = ListKW.applicative()

    @Provides
    fun listKWMonad(): Monad<ListKWHK> = ListKW.monad()

    @Provides
    fun listKWFoldable(): Foldable<ListKWHK> = ListKW.foldable()

    @Provides
    fun listKWTraverse(): Traverse<ListKWHK> = ListKW.traverse()

    @Provides
    fun listKWSemigroupK(): SemigroupK<ListKWHK> = ListKW.semigroupK()

    @Provides
    fun listKWMonoidK(): MonoidK<ListKWHK> = ListKW.monoidK()

}
