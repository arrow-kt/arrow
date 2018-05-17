package arrow.dagger.instances

import arrow.data.*
import arrow.typeclasses.*
import dagger.Module
import dagger.Provides

@Module
class NonEmptyListInstances {

  @Provides
  fun nonEmptyListFunctor(): Functor<ForNonEmptyList> = NonEmptyList.functor()

  @Provides
  fun nonEmptyListInvariant(): Invariant<ForNonEmptyList> = NonEmptyList.invariant()

  @Provides
  fun nonEmptyListContravariant(): Contravariant<ForNonEmptyList> = NonEmptyList.contravariant()

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