package arrow.dagger.extensions

import arrow.core.*
import arrow.core.extensions.id.applicative.applicative
import arrow.core.extensions.id.bimonad.bimonad
import arrow.core.extensions.id.comonad.comonad
import arrow.core.extensions.id.functor.functor
import arrow.core.extensions.id.monad.monad
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
