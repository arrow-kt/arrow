package arrow.dagger.instances

import arrow.data.*
import arrow.typeclasses.*
import dagger.Module
import dagger.Provides

@Module
class SequenceKInstances {

  @Provides
  fun sequenceKFunctor(): Functor<ForSequenceK> = SequenceK.functor()

  @Provides
  fun sequenceKApplicative(): Applicative<ForSequenceK> = SequenceK.applicative()

  @Provides
  fun sequenceKMonad(): Monad<ForSequenceK> = SequenceK.monad()

  @Provides
  fun sequenceKFoldable(): Foldable<ForSequenceK> = SequenceK.foldable()

  @Provides
  fun sequenceKTraverse(): Traverse<ForSequenceK> = SequenceK.traverse()

  @Provides
  fun sequenceKMonoidK(): MonoidK<ForSequenceK> = SequenceK.monoidK()

  @Provides
  fun sequenceKSemigroupK(): SemigroupK<ForSequenceK> = SequenceK.semigroupK()

}
