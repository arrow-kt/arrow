package arrow.dagger.instances

import arrow.data.KleisliPartialOf
import arrow.effects.typeclasses.Bracket
import arrow.instances.KleisliApplicativeInstance
import arrow.instances.KleisliBracketInstance
import arrow.instances.KleisliFunctorInstance
import arrow.instances.KleisliMonadErrorInstance
import arrow.instances.KleisliMonadInstance
import arrow.typeclasses.Applicative
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
import arrow.typeclasses.MonadError
import dagger.Module
import dagger.Provides
import javax.inject.Inject

@Module
abstract class KleisliInstances<F, D, E> {

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

  @Provides
  fun kleisliBracket(ev: DaggerKleisliBracketInstance<F, D, E>): Bracket<KleisliPartialOf<F, D>, E> = ev
}

class DaggerKleisliFunctorInstance<F, L> @Inject constructor(val FF: Functor<F>) : KleisliFunctorInstance<F, L> {
  override fun FFF(): Functor<F> = FF
}

class DaggerKleisliApplicativeInstance<F, L> @Inject constructor(val AF: Applicative<F>) : KleisliApplicativeInstance<F, L> {
  override fun FFF(): Applicative<F> = AF
}

class DaggerKleisliMonadInstance<F, L> @Inject constructor(val MF: Monad<F>) : KleisliMonadInstance<F, L> {
  override fun FFF(): Monad<F> = MF
}

class DaggerKleisliMonadErrorInstance<F, L> @Inject constructor(val MF: MonadError<F, L>) : KleisliMonadErrorInstance<F, L, L> {
  override fun FFF(): MonadError<F, L> = MF
}

class DaggerKleisliBracketInstance<F, D, E> @Inject constructor(val BFE: Bracket<F, E>) : KleisliBracketInstance<F, D, E> {
  override fun FFF(): Bracket<F, E> = BFE
}
