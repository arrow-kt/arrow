package arrow.dagger.instances

import arrow.data.*
import arrow.typeclasses.*
import dagger.Module
import dagger.Provides

@Module
class SequenceKWInstances {

    @Provides
    fun sequenceKWFunctor(): Functor<ForSequenceKW> = SequenceKW.functor()

    @Provides
    fun sequenceKWApplicative(): Applicative<ForSequenceKW> = SequenceKW.applicative()

    @Provides
    fun sequenceKWMonad(): Monad<ForSequenceKW> = SequenceKW.monad()

    @Provides
    fun sequenceKWFoldable(): Foldable<ForSequenceKW> = SequenceKW.foldable()

    @Provides
    fun sequenceKWTraverse(): Traverse<ForSequenceKW> = SequenceKW.traverse()

    @Provides
    fun sequenceKWMonoidK(): MonoidK<ForSequenceKW> = SequenceKW.monoidK()

    @Provides
    fun sequenceKWSemigroupK(): SemigroupK<ForSequenceKW> = SequenceKW.semigroupK()

}
