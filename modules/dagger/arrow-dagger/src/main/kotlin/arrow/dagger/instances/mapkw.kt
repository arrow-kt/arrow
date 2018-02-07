package arrow.dagger.instances

import arrow.data.MapKWPartialOf
import arrow.instances.*
import arrow.typeclasses.*
import dagger.Module
import dagger.Provides
import javax.inject.Inject

@Module
abstract class MapKWInstances<L> {

    @Provides
    fun mapKWFunctor(ev: DaggerMapKWFunctorInstance<L>): Functor<MapKWPartialOf<L>> = ev

    @Provides
    fun mapKWFoldable(ev: DaggerMapKWFoldableInstance<L>): Foldable<MapKWPartialOf<L>> = ev

    @Provides
    fun mapKWTraverse(ev: DaggerMapKWTraverseInstance<L>): Traverse<MapKWPartialOf<L>> = ev

}

class DaggerMapKWFunctorInstance<K> @Inject constructor() : MapKWFunctorInstance<K>

class DaggerMapKWFoldableInstance<K> @Inject constructor() : MapKWFoldableInstance<K>

class DaggerMapKWTraverseInstance<K> @Inject constructor() : MapKWTraverseInstance<K>

class DaggerMapKWSemigroupInstance<K, A> @Inject constructor(val SG: Semigroup<A>) : MapKWSemigroupInstance<K, A> {
    override fun SG(): Semigroup<A> = SG
}

class DaggerMapKWMonoidInstance<K, A> @Inject constructor(val SG: Semigroup<A>) : MapKWMonoidInstance<K, A> {
    override fun SG(): Semigroup<A> = SG
}

class DaggerMapKWEqInstance<K, A> @Inject constructor(val EQK: Eq<K>, val EQA: Eq<A>) : MapKWEqInstance<K, A> {
    override fun EQK(): Eq<K> = EQK

    override fun EQA(): Eq<A> = EQA
}