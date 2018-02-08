package arrow.dagger.instances

import arrow.data.MapKPartialOf
import arrow.instances.*
import arrow.typeclasses.*
import dagger.Module
import dagger.Provides
import javax.inject.Inject

@Module
abstract class MapKInstances<L> {

    @Provides
    fun mapKFunctor(ev: DaggerMapKFunctorInstance<L>): Functor<MapKPartialOf<L>> = ev

    @Provides
    fun mapKFoldable(ev: DaggerMapKFoldableInstance<L>): Foldable<MapKPartialOf<L>> = ev

    @Provides
    fun mapKTraverse(ev: DaggerMapKTraverseInstance<L>): Traverse<MapKPartialOf<L>> = ev

}

class DaggerMapKFunctorInstance<K> @Inject constructor() : MapKFunctorInstance<K>

class DaggerMapKFoldableInstance<K> @Inject constructor() : MapKFoldableInstance<K>

class DaggerMapKTraverseInstance<K> @Inject constructor() : MapKTraverseInstance<K>

class DaggerMapKSemigroupInstance<K, A> @Inject constructor(val SG: Semigroup<A>) : MapKSemigroupInstance<K, A> {
    override fun SG(): Semigroup<A> = SG
}

class DaggerMapKMonoidInstance<K, A> @Inject constructor(val SG: Semigroup<A>) : MapKMonoidInstance<K, A> {
    override fun SG(): Semigroup<A> = SG
}

class DaggerMapKEqInstance<K, A> @Inject constructor(val EQK: Eq<K>, val EQA: Eq<A>) : MapKEqInstance<K, A> {
    override fun EQK(): Eq<K> = EQK

    override fun EQA(): Eq<A> = EQA
}