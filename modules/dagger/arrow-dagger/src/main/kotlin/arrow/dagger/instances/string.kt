package arrow.dagger.instances

import arrow.instances.*
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