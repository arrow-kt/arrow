package arrow.dagger.instances

import arrow.instances.StringEqInstance
import arrow.instances.StringMonoidInstance
import arrow.instances.StringSemigroupInstance
import arrow.typeclasses.Eq
import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup
import dagger.Module
import dagger.Provides

@Module
class StringInstances {

    @Provides
    fun stringSemigroup(): Semigroup<String> = StringSemigroupInstance

    @Provides
    fun stringMonoid(): Monoid<String> = StringMonoidInstance

    @Provides
    fun stringEq(): Eq<String> = StringEqInstance
}