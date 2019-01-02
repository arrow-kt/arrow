package arrow.dagger.extensions

import arrow.core.extensions.eq
import arrow.core.extensions.monoid
import arrow.core.extensions.semigroup
import arrow.typeclasses.Eq
import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup
import dagger.Module
import dagger.Provides

@Module
class StringInstances {

  @Provides
  fun stringSemigroup(): Semigroup<String> = String.semigroup()

  @Provides
  fun stringMonoid(): Monoid<String> = String.monoid()

  @Provides
  fun stringEq(): Eq<String> = String.eq()

}