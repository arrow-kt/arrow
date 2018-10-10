package arrow.dagger.instances

import arrow.data.*
import arrow.instances.syntax.listk.applicative.applicative
import arrow.instances.syntax.listk.foldable.foldable
import arrow.instances.syntax.listk.functor.functor
import arrow.instances.syntax.listk.monad.monad
import arrow.instances.syntax.listk.monoidK.monoidK
import arrow.instances.syntax.listk.semigroupK.semigroupK
import arrow.instances.syntax.listk.traverse.traverse
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
