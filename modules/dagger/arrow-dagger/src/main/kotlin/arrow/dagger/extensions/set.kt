package arrow.dagger.extensions

import arrow.data.*
import arrow.data.extensions.SetKEqInstance
import arrow.data.extensions.SetKMonoidInstance
import arrow.data.extensions.SetKSemigroupInstance
import arrow.data.extensions.setk.foldable.foldable
import arrow.data.extensions.setk.monoidK.monoidK
import arrow.data.extensions.setk.semigroupK.semigroupK
import arrow.typeclasses.Eq
import arrow.typeclasses.Foldable
import arrow.typeclasses.MonoidK
import arrow.typeclasses.SemigroupK
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
