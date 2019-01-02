package arrow.dagger.extensions

import arrow.data.*
import arrow.data.extensions.sequencek.applicative.applicative
import arrow.data.extensions.sequencek.foldable.foldable
import arrow.data.extensions.sequencek.functor.functor
import arrow.data.extensions.sequencek.monad.monad
import arrow.data.extensions.sequencek.monoidK.monoidK
import arrow.data.extensions.sequencek.semigroupK.semigroupK
import arrow.data.extensions.sequencek.traverse.traverse
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
