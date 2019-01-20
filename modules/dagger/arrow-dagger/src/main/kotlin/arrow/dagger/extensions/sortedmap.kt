package arrow.dagger.extensions

import arrow.data.SortedMapKPartialOf
import arrow.data.extensions.*
import arrow.typeclasses.Foldable
import arrow.typeclasses.Functor
import arrow.typeclasses.Semigroup
import arrow.typeclasses.Traverse
import dagger.Module
import dagger.Provides
import javax.inject.Inject

@Module
abstract class SortedMapKInstances<K : Comparable<K>> {

  @Provides
  fun sortedMapKFunctor(ev: DaggerSortedMapKFunctor<K>): Functor<SortedMapKPartialOf<K>> = ev

  @Provides
  fun sortedMapKFoldable(ev: DaggerSortedMapKFoldable<K>): Foldable<SortedMapKPartialOf<K>> = ev

  @Provides
  fun sortedMapKTraverse(ev: DaggerSortedMapKTraverse<K>): Traverse<SortedMapKPartialOf<K>> = ev

}

class DaggerSortedMapKFunctor<K : Comparable<K>> @Inject constructor() : SortedMapKFunctor<K>

class DaggerSortedMapKFoldable<K : Comparable<K>> @Inject constructor() : SortedMapKFoldable<K>

class DaggerSortedMapKTraverse<K : Comparable<K>> @Inject constructor() : SortedMapKTraverse<K>

class DaggerSortedMapKSemigroup<K : Comparable<K>, A> @Inject constructor(val SG: Semigroup<A>) : SortedMapKSemigroup<K, A> {
  override fun SG(): Semigroup<A> = SG
}

class DaggerSortedMapKMonoid<K : Comparable<K>, A> @Inject constructor(val SG: Semigroup<A>) : SortedMapKMonoid<K, A> {
  override fun SG(): Semigroup<A> = SG
}