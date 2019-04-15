package arrow.dagger.extensions

import arrow.data.MapKPartialOf
import arrow.data.extensions.MapKEq
import arrow.data.extensions.MapKFoldable
import arrow.data.extensions.MapKFunctor
import arrow.data.extensions.MapKMonoid
import arrow.data.extensions.MapKSemigroup
import arrow.data.extensions.MapKTraverse
import arrow.typeclasses.Eq
import arrow.typeclasses.Foldable
import arrow.typeclasses.Functor
import arrow.typeclasses.Semigroup
import arrow.typeclasses.Traverse
import dagger.Module
import dagger.Provides
import javax.inject.Inject

@Module
abstract class MapKInstances<L> {

  @Provides
  fun mapKFunctor(ev: DaggerMapKFunctor<L>): Functor<MapKPartialOf<L>> = ev

  @Provides
  fun mapKFoldable(ev: DaggerMapKFoldable<L>): Foldable<MapKPartialOf<L>> = ev

  @Provides
  fun mapKTraverse(ev: DaggerMapKTraverse<L>): Traverse<MapKPartialOf<L>> = ev
}

class DaggerMapKFunctor<K> @Inject constructor() : MapKFunctor<K>

class DaggerMapKFoldable<K> @Inject constructor() : MapKFoldable<K>

class DaggerMapKTraverse<K> @Inject constructor() : MapKTraverse<K>

class DaggerMapKSemigroup<K, A> @Inject constructor(val SG: Semigroup<A>) : MapKSemigroup<K, A> {
  override fun SG(): Semigroup<A> = SG
}

class DaggerMapKMonoid<K, A> @Inject constructor(val SG: Semigroup<A>) : MapKMonoid<K, A> {
  override fun SG(): Semigroup<A> = SG
}

class DaggerMapKEq<K, A> @Inject constructor(val EQK: Eq<K>, val EQA: Eq<A>) : MapKEq<K, A> {
  override fun EQK(): Eq<K> = EQK

  override fun EQA(): Eq<A> = EQA
}
