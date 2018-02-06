package arrow.dagger.instances

import arrow.core.*
import arrow.typeclasses.*
import dagger.Module
import dagger.Provides

@Module
class IdInstances {

    @Provides
    fun idFunctor(): Functor<IdHK> = Id.functor()

    @Provides
    fun idApplicative(): Applicative<IdHK> = Id.applicative()

    @Provides
    fun idMonad(): Monad<IdHK> = Id.monad()

    @Provides
    fun idComonad(): Comonad<IdHK> = Id.comonad()

    @Provides
    fun idBimonad(): Bimonad<IdHK> = Id.bimonad()

}
