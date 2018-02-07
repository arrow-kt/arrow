package arrow.dagger.instances

import arrow.data.*
import arrow.instances.SetKWEqInstance
import arrow.instances.SetKWMonoidInstance
import arrow.instances.SetKWSemigroupInstance
import arrow.typeclasses.Eq
import arrow.typeclasses.Foldable
import arrow.typeclasses.MonoidK
import arrow.typeclasses.SemigroupK
import dagger.Module
import dagger.Provides
import javax.inject.Inject

@Module
class SetKWInstances {

    @Provides
    fun setKWFoldable(): Foldable<ForSetKW> = SetKW.foldable()

    @Provides
    fun setKWMonoidK(): MonoidK<ForSetKW> = SetKW.monoidK()

    @Provides
    fun setKWSemigroupK(): SemigroupK<ForSetKW> = SetKW.semigroupK()

}

class DaggerSetKWSemigroupInstance<A> : SetKWSemigroupInstance<A>

class DaggerSetKWMonoidInstance<A> : SetKWMonoidInstance<A>

class DaggerSetKWEqInstance<A> @Inject constructor(val eqA: Eq<A>) : SetKWEqInstance<A> {
    override fun EQ(): Eq<A> = eqA
}
