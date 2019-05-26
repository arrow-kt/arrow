package arrow.dagger.extensions

import arrow.data.ValidatedPartialOf
import arrow.data.extensions.ValidatedApplicative
import arrow.data.extensions.ValidatedEq
import arrow.data.extensions.ValidatedFoldable
import arrow.data.extensions.ValidatedFunctor
import arrow.data.extensions.ValidatedSemigroupK
import arrow.data.extensions.ValidatedTraverse
import arrow.core.typeclasses.Applicative
import arrow.core.typeclasses.Eq
import arrow.core.typeclasses.Foldable
import arrow.core.typeclasses.Functor
import arrow.core.typeclasses.Semigroup
import arrow.core.typeclasses.SemigroupK
import arrow.core.typeclasses.Traverse
import dagger.Module
import dagger.Provides
import javax.inject.Inject

@Module
abstract class ValidatedInstances<L> {

  @Provides
  fun validatedFunctor(ev: DaggerValidatedFunctor<L>): Functor<ValidatedPartialOf<L>> = ev

  @Provides
  fun validatedApplicative(ev: DaggerValidatedApplicative<L>): Applicative<ValidatedPartialOf<L>> = ev

  @Provides
  fun validatedFoldable(ev: DaggerValidatedFoldable<L>): Foldable<ValidatedPartialOf<L>> = ev

  @Provides
  fun validatedTraverse(ev: DaggerValidatedTraverse<L>): Traverse<ValidatedPartialOf<L>> = ev

  @Provides
  fun validatedSemigroupK(ev: DaggerValidatedSemigroupK<L>): SemigroupK<ValidatedPartialOf<L>> = ev
}

class DaggerValidatedFunctor<F> @Inject constructor() : ValidatedFunctor<F>

class DaggerValidatedApplicative<F> @Inject constructor(val SE: Semigroup<F>) : ValidatedApplicative<F> {
  override fun SE(): Semigroup<F> = SE
}

class DaggerValidatedFoldable<F> @Inject constructor() : ValidatedFoldable<F>

class DaggerValidatedTraverse<F> @Inject constructor() : ValidatedTraverse<F>

class DaggerValidatedSemigroupK<F> @Inject constructor(val SE: Semigroup<F>) : ValidatedSemigroupK<F> {
  override fun SE(): Semigroup<F> = SE
}

class DaggerValidatedEq<L, R> @Inject constructor(val eqL: Eq<L>, val eqR: Eq<R>) : ValidatedEq<L, R> {
  override fun EQL(): Eq<L> = eqL
  override fun EQR(): Eq<R> = eqR
}
