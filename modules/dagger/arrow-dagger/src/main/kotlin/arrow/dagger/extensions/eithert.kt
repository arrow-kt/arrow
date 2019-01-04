package arrow.dagger.extensions

import arrow.data.EitherTPartialOf
import arrow.data.extensions.*
import arrow.typeclasses.*
import dagger.Module
import dagger.Provides
import javax.inject.Inject

@Module
abstract class EitherTInstances<F, L> {

  @Provides
  fun eitherTFunctor(ev: DaggerEitherTFunctor<F, L>): Functor<EitherTPartialOf<F, L>> = ev

  @Provides
  fun eitherTApplicative(ev: DaggerEitherTApplicative<F, L>): Applicative<EitherTPartialOf<F, L>> = ev

  @Provides
  fun eitherTMonad(ev: DaggerEitherTMonad<F, L>): Monad<EitherTPartialOf<F, L>> = ev

  @Provides
  fun eitherTApplicativeError(ev: DaggerEitherTMonadError<F, L>): ApplicativeError<EitherTPartialOf<F, L>, L> = ev

  @Provides
  fun eitherTMonadError(ev: DaggerEitherTMonadError<F, L>): MonadError<EitherTPartialOf<F, L>, L> = ev

  @Provides
  fun eitherTFoldable(ev: DaggerEitherTFoldable<F, L>): Foldable<EitherTPartialOf<F, L>> = ev

  @Provides
  fun eitherTTraverse(ev: DaggerEitherTTraverse<F, L>): Traverse<EitherTPartialOf<F, L>> = ev

  @Provides
  fun eitherTSemigroupK(ev: DaggerEitherTSemigroupK<F, L>): SemigroupK<EitherTPartialOf<F, L>> = ev

}

class DaggerEitherTFunctor<F, L> @Inject constructor(val FF: Functor<F>) : EitherTFunctor<F, L> {
  override fun FF(): Functor<F> = FF
}

class DaggerEitherTApplicative<F, L> @Inject constructor(val AF: Applicative<F>) : EitherTApplicative<F, L> {
  override fun AF(): Applicative<F> = AF
  override fun FF(): Functor<F> = AF
}

class DaggerEitherTMonad<F, L> @Inject constructor(val MF: Monad<F>) : EitherTMonad<F, L> {
  override fun FF(): Functor<F> = MF
  override fun MF(): Monad<F> = MF
}

class DaggerEitherTMonadError<F, L> @Inject constructor(val ME: MonadError<F, L>) : EitherTMonadError<F, L> {
  override fun FF(): Functor<F> = ME
  override fun MF(): Monad<F> = ME
  override fun AE(): ApplicativeError<F, L> = ME
}

class DaggerEitherTFoldable<F, L> @Inject constructor(val FFF: Foldable<F>) : EitherTFoldable<F, L> {
  override fun FFF(): Foldable<F> = FFF
}

class DaggerEitherTTraverse<F, L> @Inject constructor(val FFF: Traverse<F>) : EitherTTraverse<F, L> {
  override fun FF(): Functor<F> = FFF
  override fun FFF(): Foldable<F> = FFF
  override fun TF(): Traverse<F> = FFF
}

class DaggerEitherTSemigroupK<F, L> @Inject constructor(val MF: Monad<F>) : EitherTSemigroupK<F, L> {
  override fun MF(): Monad<F> = MF
}
