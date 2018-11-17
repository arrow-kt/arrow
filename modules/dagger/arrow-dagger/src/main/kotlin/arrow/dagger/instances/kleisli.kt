package arrow.dagger.instances

import arrow.data.KleisliPartialOf
import arrow.instances.KleisliApplicativeInstance
import arrow.instances.KleisliFunctorInstance
import arrow.instances.KleisliMonadErrorInstance
import arrow.instances.KleisliMonadInstance
import arrow.typeclasses.*
import dagger.Module
import dagger.Provides
import javax.inject.Inject

@Module
abstract class KleisliInstances<F, D> {

  @Provides
  fun kleisliFunctor(ev: DaggerKleisliFunctorInstance<F, D>): Functor<KleisliPartialOf<F, D>> = ev

  @Provides
  fun kleisliApplicative(ev: DaggerKleisliApplicativeInstance<F, D>): Applicative<KleisliPartialOf<F, D>> = ev

  @Provides
  fun kleisliMonad(ev: DaggerKleisliMonadInstance<F, D>): Monad<KleisliPartialOf<F, D>> = ev

  @Provides
  fun kleisliApplicativeError(ev: DaggerKleisliMonadErrorInstance<F, D>): ApplicativeError<KleisliPartialOf<F, D>, D> = ev

  @Provides
  fun kleisliMonadError(ev: DaggerKleisliMonadErrorInstance<F, D>): MonadError<KleisliPartialOf<F, D>, D> = ev
}

class DaggerKleisliFunctorInstance<F, L> @Inject constructor(val FF: Functor<F>) : KleisliFunctorInstance<F, L> {
  override fun FF(): Functor<F> = FF
}

class DaggerKleisliApplicativeInstance<F, L> @Inject constructor(val AF: Applicative<F>) : KleisliApplicativeInstance<F, L> {
  override fun AF(): Applicative<F> = AF
}

class DaggerKleisliMonadInstance<F, L> @Inject constructor(val MF: Monad<F>) : KleisliMonadInstance<F, L> {
  override fun MF(): Monad<F> = MF
}

class DaggerKleisliMonadErrorInstance<F, L> @Inject constructor(val MF: MonadError<F, L>) : KleisliMonadErrorInstance<F, L, L> {
  override fun ME(): MonadError<F, L> = MF
}
