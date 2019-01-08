package arrow.dagger.extensions

import arrow.data.*
import arrow.data.extensions.SetKEq
import arrow.data.extensions.SetKMonoid
import arrow.data.extensions.SetKSemigroup
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

class DaggerSetKSemigroup<A> : SetKSemigroup<A>

class DaggerSetKMonoid<A> : SetKMonoid<A>

class DaggerSetKEq<A> @Inject constructor(val eqA: Eq<A>) : SetKEq<A> {
  override fun EQ(): Eq<A> = eqA
}
