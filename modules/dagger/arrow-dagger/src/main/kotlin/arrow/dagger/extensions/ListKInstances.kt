package arrow.dagger.extensions

import arrow.data.ForListK
import arrow.data.ListK
import arrow.data.extensions.listk.applicative.applicative
import arrow.data.extensions.listk.foldable.foldable
import arrow.data.extensions.listk.functor.functor
import arrow.data.extensions.listk.monad.monad
import arrow.data.extensions.listk.monoidK.monoidK
import arrow.data.extensions.listk.semigroupK.semigroupK
import arrow.data.extensions.listk.traverse.traverse
import arrow.core.typeclasses.Applicative
import arrow.core.typeclasses.Foldable
import arrow.core.typeclasses.Functor
import arrow.core.typeclasses.Monad
import arrow.core.typeclasses.MonoidK
import arrow.core.typeclasses.SemigroupK
import arrow.core.typeclasses.Traverse
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
