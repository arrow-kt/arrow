package arrow.dagger.instances

import arrow.core.*
import arrow.instances.function0.applicative.applicative
import arrow.instances.function0.bimonad.bimonad
import arrow.instances.function0.comonad.comonad
import arrow.instances.function0.functor.functor
import arrow.instances.function0.monad.monad
import arrow.typeclasses.*
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
