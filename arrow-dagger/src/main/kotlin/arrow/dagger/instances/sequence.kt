package arrow.dagger.instances

import arrow.data.*
import arrow.typeclasses.*
import dagger.Module
import dagger.Provides

@Module
class SequenceKWInstances {

    @Provides
    fun sequenceKWFunctor(): Functor<SequenceKWHK> = SequenceKW.functor()

    @Provides
    fun sequenceKWApplicative(): Applicative<SequenceKWHK> = SequenceKW.applicative()

    @Provides
    fun sequenceKWMonad(): Monad<SequenceKWHK> = SequenceKW.monad()

    @Provides
    fun sequenceKWFoldable(): Foldable<SequenceKWHK> = SequenceKW.foldable()

    @Provides
    fun sequenceKWTraverse(): Traverse<SequenceKWHK> = SequenceKW.traverse()

    @Provides
    fun sequenceKWMonoidK(): MonoidK<SequenceKWHK> = SequenceKW.monoidK()

    @Provides
    fun sequenceKWSemigroupK(): SemigroupK<SequenceKWHK> = SequenceKW.semigroupK()

}
