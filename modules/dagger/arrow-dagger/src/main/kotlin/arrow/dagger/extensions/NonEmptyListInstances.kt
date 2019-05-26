package arrow.dagger.extensions

import arrow.data.ForNonEmptyList
import arrow.data.NonEmptyList
import arrow.data.extensions.nonemptylist.applicative.applicative
import arrow.data.extensions.nonemptylist.bimonad.bimonad
import arrow.data.extensions.nonemptylist.comonad.comonad
import arrow.data.extensions.nonemptylist.foldable.foldable
import arrow.data.extensions.nonemptylist.functor.functor
import arrow.data.extensions.nonemptylist.monad.monad
import arrow.data.extensions.nonemptylist.semigroupK.semigroupK
import arrow.data.extensions.nonemptylist.traverse.traverse
import arrow.core.typeclasses.Applicative
import arrow.core.typeclasses.Bimonad
import arrow.core.typeclasses.Comonad
import arrow.core.typeclasses.Foldable
import arrow.core.typeclasses.Functor
import arrow.core.typeclasses.Monad
import arrow.core.typeclasses.SemigroupK
import arrow.core.typeclasses.Traverse
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
