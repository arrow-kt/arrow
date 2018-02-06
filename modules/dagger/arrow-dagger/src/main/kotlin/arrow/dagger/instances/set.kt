package arrow.dagger.instances

import arrow.data.*
import arrow.instances.SetKWEqInstance
import arrow.instances.SetKWMonoidInstance
import arrow.instances.SetKWSemigroupInstance
import arrow.typeclasses.*
import dagger.*
import javax.inject.*
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
