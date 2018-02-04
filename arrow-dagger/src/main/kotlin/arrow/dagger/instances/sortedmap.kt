package arrow.dagger.instances

import arrow.data.SortedMapKWKindPartial
import arrow.instances.*
import arrow.typeclasses.Foldable
import arrow.typeclasses.Functor
import arrow.typeclasses.Semigroup
import arrow.typeclasses.Traverse
import dagger.Module
import dagger.Provides
import javax.inject.Inject

@Module
abstract class SortedMapKWInstances<K : Comparable<K>> {

    @Provides
    fun sortedMapKWFunctor(ev: DaggerSortedMapKWFunctorInstance<K>): Functor<SortedMapKWKindPartial<K>> = ev

    @Provides
    fun sortedMapKWFoldable(ev: DaggerSortedMapKWFoldableInstance<K>): Foldable<SortedMapKWKindPartial<K>> = ev

    @Provides
    fun sortedMapKWTraverse(ev: DaggerSortedMapKWTraverseInstance<K>): Traverse<SortedMapKWKindPartial<K>> = ev

}

class DaggerSortedMapKWFunctorInstance<K : Comparable<K>> @Inject constructor() : SortedMapKWFunctorInstance<K>

class DaggerSortedMapKWFoldableInstance<K : Comparable<K>> @Inject constructor() : SortedMapKWFoldableInstance<K>

class DaggerSortedMapKWTraverseInstance<K : Comparable<K>> @Inject constructor() : SortedMapKWTraverseInstance<K>

class DaggerSortedMapKWSemigroupInstance<K : Comparable<K>, A> @Inject constructor(val SG: Semigroup<A>) : SortedMapKWSemigroupInstance<K, A> {
    override fun SG(): Semigroup<A> = SG
}

class DaggerSortedMapKWMonoidInstance<K : Comparable<K>, A> @Inject constructor(val SG: Semigroup<A>) : SortedMapKWMonoidInstance<K, A> {
    override fun SG(): Semigroup<A> = SG
}