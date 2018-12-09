package arrow.dagger.instances

import arrow.data.EitherTPartialOf
import arrow.instances.*
import arrow.typeclasses.*
import dagger.Module
import dagger.Provides
import javax.inject.Inject

@Module
abstract class EitherTInstances<F, L> {

  @Provides
  fun eitherTFunctor(ev: DaggerEitherTFunctorInstance<F, L>): Functor<EitherTPartialOf<F, L>> = ev

  @Provides
  fun eitherTApplicative(ev: DaggerEitherTApplicativeInstance<F, L>): Applicative<EitherTPartialOf<F, L>> = ev

  @Provides
  fun eitherTMonad(ev: DaggerEitherTMonadInstance<F, L>): Monad<EitherTPartialOf<F, L>> = ev

  @Provides
  fun eitherTApplicativeError(ev: DaggerEitherTMonadErrorInstance<F, L>): ApplicativeError<EitherTPartialOf<F, L>, L> = ev

  @Provides
  fun eitherTMonadError(ev: DaggerEitherTMonadErrorInstance<F, L>): MonadError<EitherTPartialOf<F, L>, L> = ev

  @Provides
  fun eitherTFoldable(ev: DaggerEitherTFoldableInstance<F, L>): Foldable<EitherTPartialOf<F, L>> = ev

  @Provides
  fun eitherTTraverse(ev: DaggerEitherTTraverseInstance<F, L>): Traverse<EitherTPartialOf<F, L>> = ev

  @Provides
  fun eitherTSemigroupK(ev: DaggerEitherTSemigroupKInstance<F, L>): SemigroupK<EitherTPartialOf<F, L>> = ev

}

class DaggerEitherTFunctorInstance<F, L> @Inject constructor(val FF: Functor<F>) : EitherTFunctorInstance<F, L> {
  override fun FF(): Functor<F> = FF
}

class DaggerEitherTApplicativeInstance<F, L> @Inject constructor(val AF: Applicative<F>) : EitherTApplicativeInstance<F, L> {
  override fun AF(): Applicative<F> = AF
  override fun FF(): Functor<F> = AF
}

class DaggerEitherTMonadInstance<F, L> @Inject constructor(val MF: Monad<F>) : EitherTMonadInstance<F, L> {
  override fun FF(): Functor<F> = MF
  override fun MF(): Monad<F> = MF
}

class DaggerEitherTMonadErrorInstance<F, L> @Inject constructor(val ME: MonadError<F, L>) : EitherTMonadErrorInstance<F, L> {
  override fun FF(): Functor<F> = ME
  override fun MF(): Monad<F> = ME
  override fun AE(): ApplicativeError<F, L> = ME
}

class DaggerEitherTFoldableInstance<F, L> @Inject constructor(val FFF: Foldable<F>) : EitherTFoldableInstance<F, L> {
  override fun FFF(): Foldable<F> = FFF
}

class DaggerEitherTTraverseInstance<F, L> @Inject constructor(val FFF: Traverse<F>) : EitherTTraverseInstance<F, L> {
  override fun FF(): Functor<F> = FFF
  override fun FFF(): Foldable<F> = FFF
  override fun TF(): Traverse<F> = FFF
}

class DaggerEitherTSemigroupKInstance<F, L> @Inject constructor(val MF: Monad<F>) : EitherTSemigroupKInstance<F, L> {
  override fun MF(): Monad<F> = MF
}
