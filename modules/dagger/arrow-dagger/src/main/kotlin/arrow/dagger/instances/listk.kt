package arrow.dagger.instances

import arrow.data.*
import arrow.instances.listk.applicative.applicative
import arrow.instances.listk.foldable.foldable
import arrow.instances.listk.functor.functor
import arrow.instances.listk.monad.monad
import arrow.instances.listk.monoidK.monoidK
import arrow.instances.listk.semigroupK.semigroupK
import arrow.instances.listk.traverse.traverse
import arrow.typeclasses.*
import dagger.Module
import dagger.Provides

@Module
class ListKInstances {

  @Provides
  fun listKFunctor(): Functor<ForListK> = ListK.functor()

  @Provides
  fun listKApplicative(): Applicative<ForListK> = ListK.applicative()

  @Provides
  fun listKMonad(): Monad<ForListK> = ListK.monad()

  @Provides
  fun listKFoldable(): Foldable<ForListK> = ListK.foldable()

  @Provides
  fun listKTraverse(): Traverse<ForListK> = ListK.traverse()

  @Provides
  fun listKSemigroupK(): SemigroupK<ForListK> = ListK.semigroupK()

  @Provides
  fun listKMonoidK(): MonoidK<ForListK> = ListK.monoidK()

}
