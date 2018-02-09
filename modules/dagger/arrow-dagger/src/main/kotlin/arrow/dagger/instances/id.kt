package arrow.dagger.instances

import arrow.core.*
import arrow.typeclasses.*
import dagger.Module
import dagger.Provides

@Module
class IdInstances {

    @Provides
    fun idFunctor(): Functor<ForId> = Id.functor()

    @Provides
    fun idApplicative(): Applicative<ForId> = Id.applicative()

    @Provides
    fun idMonad(): Monad<ForId> = Id.monad()

    @Provides
    fun idComonad(): Comonad<ForId> = Id.comonad()

    @Provides
    fun idBimonad(): Bimonad<ForId> = Id.bimonad()

}
