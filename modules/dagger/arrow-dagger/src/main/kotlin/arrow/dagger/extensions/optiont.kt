package arrow.dagger.extensions

import arrow.data.OptionTPartialOf
import arrow.data.extensions.*
import arrow.typeclasses.*
import dagger.Module
import dagger.Provides
import javax.inject.Inject

@Module
abstract class OptionTInstances<F> {

  @Provides
  fun optionTFunctor(ev: DaggerOptionTFunctor<F>): Functor<OptionTPartialOf<F>> = ev

  @Provides
  fun optionTApplicative(ev: DaggerOptionTApplicative<F>): Applicative<OptionTPartialOf<F>> = ev

  @Provides
  fun optionTMonad(ev: DaggerOptionTMonad<F>): Monad<OptionTPartialOf<F>> = ev

  @Provides
  fun optionTFoldable(ev: DaggerOptionTFoldable<F>): Foldable<OptionTPartialOf<F>> = ev

  @Provides
  fun optionTTraverse(ev: DaggerOptionTTraverse<F>): Traverse<OptionTPartialOf<F>> = ev

  @Provides
  fun optionTSemigroupK(ev: DaggerOptionTSemigroupK<F>): SemigroupK<OptionTPartialOf<F>> = ev

  @Provides
  fun optionTMonoidK(ev: DaggerOptionTMonoidK<F>): MonoidK<OptionTPartialOf<F>> = ev

}

class DaggerOptionTFunctor<F> @Inject constructor(val FF: Functor<F>) : OptionTFunctor<F> {
  override fun FF(): Functor<F> = FF
}

class DaggerOptionTApplicative<F> @Inject constructor(val AF: Applicative<F>) : OptionTApplicative<F> {
  override fun FF(): Functor<F> = AF
  override fun AF(): Applicative<F> = AF
}

class DaggerOptionTMonad<F> @Inject constructor(val FF: Monad<F>) : OptionTMonad<F> {
  override fun FF(): Monad<F> = FF
  override fun MF(): Monad<F> = FF
}

class DaggerOptionTApplicativeError<F, E> @Inject constructor(val AE: ApplicativeError<F, E> ) : OptionTApplicativeError<F, E> {
  override fun FF(): Functor<F> = AE
  override fun AF(): Applicative<F> = AE
  override fun AE(): ApplicativeError<F, E> = AE
}

class DaggerOptionTMonadError<F, E> @Inject constructor(val ME: MonadError<F, E>) : OptionTMonadError<F, E> {
  override fun FF(): Functor<F> = ME
  override fun AF(): Applicative<F> = ME
  override fun ME(): MonadError<F, E> = ME
}

class DaggerOptionTFoldable<F> @Inject constructor(val FFF: Foldable<F>) : OptionTFoldable<F> {
  override fun FFF(): Foldable<F> = FFF
}

class DaggerOptionTTraverse<F> @Inject constructor(val FFF: Traverse<F>) : OptionTTraverse<F> {
  override fun FFF(): Traverse<F> = FFF
  override fun FFT(): Traverse<F> = FFF
}

class DaggerOptionTSemigroupK<F> @Inject constructor(val FF: Monad<F>) : OptionTSemigroupK<F> {
  override fun MF(): Monad<F> = FF
}

class DaggerOptionTMonoidK<F> @Inject constructor(val FF: Monad<F>) : OptionTMonoidK<F> {
  override fun MF(): Monad<F> = FF
}