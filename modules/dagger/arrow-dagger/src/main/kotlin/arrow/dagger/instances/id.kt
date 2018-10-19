package arrow.dagger.instances

import arrow.core.*
import arrow.instances.id.applicative.applicative
import arrow.instances.id.bimonad.bimonad
import arrow.instances.id.comonad.comonad
import arrow.instances.id.functor.functor
import arrow.instances.id.monad.monad
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
