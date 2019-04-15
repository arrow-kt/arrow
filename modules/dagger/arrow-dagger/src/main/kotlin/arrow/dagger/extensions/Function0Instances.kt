package arrow.dagger.extensions

import arrow.core.ForFunction0
import arrow.core.Function0
import arrow.core.extensions.function0.applicative.applicative
import arrow.core.extensions.function0.bimonad.bimonad
import arrow.core.extensions.function0.comonad.comonad
import arrow.core.extensions.function0.functor.functor
import arrow.core.extensions.function0.monad.monad
import arrow.typeclasses.Applicative
import arrow.typeclasses.Bimonad
import arrow.typeclasses.Comonad
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
import dagger.Module
import dagger.Provides

@Module
class Function0Instances {

  @Provides
  fun function0Functor(): Functor<ForFunction0> = Function0.functor()

  @Provides
  fun function0Applicative(): Applicative<ForFunction0> = Function0.applicative()

  @Provides
  fun function0Monad(): Monad<ForFunction0> = Function0.monad()

  @Provides
  fun function0Comonad(): Comonad<ForFunction0> = Function0.comonad()

  @Provides
  fun function0Bimonad(): Bimonad<ForFunction0> = Function0.bimonad()
}
