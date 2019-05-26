package arrow.dagger.extensions

import arrow.core.ForId
import arrow.core.Id
import arrow.core.extensions.id.applicative.applicative
import arrow.core.extensions.id.bimonad.bimonad
import arrow.core.extensions.id.comonad.comonad
import arrow.core.extensions.id.functor.functor
import arrow.core.extensions.id.monad.monad
import arrow.core.typeclasses.Applicative
import arrow.core.typeclasses.Bimonad
import arrow.core.typeclasses.Comonad
import arrow.core.typeclasses.Functor
import arrow.core.typeclasses.Monad
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
