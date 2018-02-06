package arrow.dagger.instances

import arrow.data.*
import arrow.typeclasses.*
import dagger.Module
import dagger.Provides

@Module
class ListKWInstances {

    @Provides
    fun listKWFunctor(): Functor<ForListKW> = ListKW.functor()

    @Provides
    fun listKWApplicative(): Applicative<ForListKW> = ListKW.applicative()

    @Provides
    fun listKWMonad(): Monad<ForListKW> = ListKW.monad()

    @Provides
    fun listKWFoldable(): Foldable<ForListKW> = ListKW.foldable()

    @Provides
    fun listKWTraverse(): Traverse<ForListKW> = ListKW.traverse()

    @Provides
    fun listKWSemigroupK(): SemigroupK<ForListKW> = ListKW.semigroupK()

    @Provides
    fun listKWMonoidK(): MonoidK<ForListKW> = ListKW.monoidK()

}
