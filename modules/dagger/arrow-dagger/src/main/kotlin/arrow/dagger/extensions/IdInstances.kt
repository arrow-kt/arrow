package arrow.dagger.extensions

import arrow.core.ForId
import arrow.core.Id
import arrow.core.extensions.id.applicative.applicative
import arrow.core.extensions.id.bimonad.bimonad
import arrow.core.extensions.id.comonad.comonad
import arrow.core.extensions.id.functor.functor
import arrow.core.extensions.id.monad.monad
import arrow.typeclasses.Applicative
import arrow.typeclasses.Bimonad
import arrow.typeclasses.Comonad
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
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
