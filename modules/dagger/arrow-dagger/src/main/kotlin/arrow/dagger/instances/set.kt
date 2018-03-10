package arrow.dagger.instances

import arrow.data.*
import arrow.instances.SetKEqInstance
import arrow.instances.SetKHashInstance
import arrow.instances.SetKMonoidInstance
import arrow.instances.SetKSemigroupInstance
import arrow.typeclasses.*
import dagger.Module
import dagger.Provides
import javax.inject.Inject

@Module
class SetKInstances {

    @Provides
    fun setKFoldable(): Foldable<ForSetK> = SetK.foldable()

    @Provides
    fun setKMonoidK(): MonoidK<ForSetK> = SetK.monoidK()

    @Provides
    fun setKSemigroupK(): SemigroupK<ForSetK> = SetK.semigroupK()

}

class DaggerSetKSemigroupInstance<A> : SetKSemigroupInstance<A>

class DaggerSetKMonoidInstance<A> : SetKMonoidInstance<A>

class DaggerSetKEqInstance<A> @Inject constructor(val eqA: Eq<A>) : SetKEqInstance<A> {
    override fun EQ(): Eq<A> = eqA
}

class DaggerSetKHashInstance<A> @Inject constructor(val hashA: Hash<A>) : SetKHashInstance<A> {
    override fun HS(): Hash<A> = hashA
}
