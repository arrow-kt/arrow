package arrow.dagger.extensions

import arrow.data.KleisliPartialOf
import arrow.data.extensions.KleisliApplicative
import arrow.data.extensions.KleisliFunctor
import arrow.data.extensions.KleisliMonadError
import arrow.data.extensions.KleisliMonad
import arrow.typeclasses.*
import dagger.Module
import dagger.Provides
import javax.inject.Inject

@Module
abstract class KleisliInstances<F, D> {

  @Provides
  fun kleisliFunctor(ev: DaggerKleisliFunctor<F, D>): Functor<KleisliPartialOf<F, D>> = ev

  @Provides
  fun kleisliApplicative(ev: DaggerKleisliApplicative<F, D>): Applicative<KleisliPartialOf<F, D>> = ev

  @Provides
  fun kleisliMonad(ev: DaggerKleisliMonad<F, D>): Monad<KleisliPartialOf<F, D>> = ev

  @Provides
  fun kleisliApplicativeError(ev: DaggerKleisliMonadError<F, D>): ApplicativeError<KleisliPartialOf<F, D>, D> = ev

  @Provides
  fun kleisliMonadError(ev: DaggerKleisliMonadError<F, D>): MonadError<KleisliPartialOf<F, D>, D> = ev
}

class DaggerKleisliFunctor<F, L> @Inject constructor(val FF: Functor<F>) : KleisliFunctor<F, L> {
  override fun FF(): Functor<F> = FF
}

class DaggerKleisliApplicative<F, L> @Inject constructor(val AF: Applicative<F>) : KleisliApplicative<F, L> {
  override fun AF(): Applicative<F> = AF
}

class DaggerKleisliMonad<F, L> @Inject constructor(val MF: Monad<F>) : KleisliMonad<F, L> {
  override fun MF(): Monad<F> = MF
}

class DaggerKleisliMonadError<F, L> @Inject constructor(val MF: MonadError<F, L>) : KleisliMonadError<F, L, L> {
  override fun ME(): MonadError<F, L> = MF
}
