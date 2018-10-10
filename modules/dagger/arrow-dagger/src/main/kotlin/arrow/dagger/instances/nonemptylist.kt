package arrow.dagger.instances

import arrow.data.*
import arrow.instances.syntax.nonemptylist.applicative.applicative
import arrow.instances.syntax.nonemptylist.bimonad.bimonad
import arrow.instances.syntax.nonemptylist.comonad.comonad
import arrow.instances.syntax.nonemptylist.foldable.foldable
import arrow.instances.syntax.nonemptylist.functor.functor
import arrow.instances.syntax.nonemptylist.monad.monad
import arrow.instances.syntax.nonemptylist.semigroupK.semigroupK
import arrow.instances.syntax.nonemptylist.traverse.traverse
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