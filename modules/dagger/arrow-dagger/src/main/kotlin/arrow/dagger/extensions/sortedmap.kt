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
  fun sortedMapKFunctor(ev: DaggerSortedMapKFunctorInstance<K>): Functor<SortedMapKPartialOf<K>> = ev

  @Provides
  fun sortedMapKFoldable(ev: DaggerSortedMapKFoldableInstance<K>): Foldable<SortedMapKPartialOf<K>> = ev

  @Provides
  fun sortedMapKTraverse(ev: DaggerSortedMapKTraverseInstance<K>): Traverse<SortedMapKPartialOf<K>> = ev

}

class DaggerSortedMapKFunctorInstance<K : Comparable<K>> @Inject constructor() : SortedMapKFunctorInstance<K>

class DaggerSortedMapKFoldableInstance<K : Comparable<K>> @Inject constructor() : SortedMapKFoldableInstance<K>

class DaggerSortedMapKTraverseInstance<K : Comparable<K>> @Inject constructor() : SortedMapKTraverseInstance<K>

class DaggerSortedMapKSemigroupInstance<K : Comparable<K>, A> @Inject constructor(val SG: Semigroup<A>) : SortedMapKSemigroupInstance<K, A> {
  override fun SG(): Semigroup<A> = SG
}

class DaggerSortedMapKMonoidInstance<K : Comparable<K>, A> @Inject constructor(val SG: Semigroup<A>) : SortedMapKMonoidInstance<K, A> {
  override fun SG(): Semigroup<A> = SG
}