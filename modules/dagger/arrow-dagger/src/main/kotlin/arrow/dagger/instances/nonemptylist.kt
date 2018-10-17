package arrow.dagger.instances

import arrow.data.*
import arrow.instances.nonemptylist.applicative.applicative
import arrow.instances.nonemptylist.bimonad.bimonad
import arrow.instances.nonemptylist.comonad.comonad
import arrow.instances.nonemptylist.foldable.foldable
import arrow.instances.nonemptylist.functor.functor
import arrow.instances.nonemptylist.monad.monad
import arrow.instances.nonemptylist.semigroupK.semigroupK
import arrow.instances.nonemptylist.traverse.traverse
import arrow.typeclasses.*
import dagger.Module
import dagger.Provides

@Module
class NonEmptyListInstances {

  @Provides
  fun nonEmptyListFunctor(): Functor<ForNonEmptyList> = NonEmptyList.functor()

  @Provides
  fun nonEmptyListApplicative(): Applicative<ForNonEmptyList> = NonEmptyList.applicative()

  @Provides
  fun nonEmptyListMonad(): Monad<ForNonEmptyList> = NonEmptyList.monad()

  @Provides
  fun nonEmptyListFoldable(): Foldable<ForNonEmptyList> = NonEmptyList.foldable()

  @Provides
  fun nonEmptyListTraverse(): Traverse<ForNonEmptyList> = NonEmptyList.traverse()

  @Provides
  fun nonEmptyListSemigroupK(): SemigroupK<ForNonEmptyList> = NonEmptyList.semigroupK()

  @Provides
  fun nonEmptyListComonad(): Comonad<ForNonEmptyList> = NonEmptyList.comonad()

  @Provides
  fun nonEmptyListBimonad(): Bimonad<ForNonEmptyList> = NonEmptyList.bimonad()

}