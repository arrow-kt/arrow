package arrow.dagger.instances

import arrow.data.KleisliKindPartial
import arrow.instances.*
import arrow.typeclasses.*
import dagger.Module
import dagger.Provides
import javax.inject.Inject

@Module
abstract class KleisliInstances<F, D> {

    @Provides
    fun kleisliFunctor(ev: DaggerKleisliFunctorInstance<F, D>): Functor<KleisliKindPartial<F, D>> = ev

    @Provides
    fun kleisliApplicative(ev: DaggerKleisliApplicativeInstance<F, D>): Applicative<KleisliKindPartial<F, D>> = ev

    @Provides
    fun kleisliMonad(ev: DaggerKleisliMonadInstance<F, D>): Monad<KleisliKindPartial<F, D>> = ev

    @Provides
    fun kleisliApplicativeError(ev: DaggerKleisliMonadErrorInstance<F, D>): ApplicativeError<KleisliKindPartial<F, D>, D> = ev

    @Provides
    fun kleisliMonadError(ev: DaggerKleisliMonadErrorInstance<F, D>): MonadError<KleisliKindPartial<F, D>, D> = ev
}

class DaggerKleisliFunctorInstance<F, L> @Inject constructor(val FF: Functor<F>) : KleisliFunctorInstance<F, L> {
    override fun FF(): Functor<F> = FF
}

class DaggerKleisliApplicativeInstance<F, L> @Inject constructor(val AF: Applicative<F>) : KleisliApplicativeInstance<F, L> {
    override fun FF(): Applicative<F> = AF
}

class DaggerKleisliMonadInstance<F, L> @Inject constructor(val MF: Monad<F>) : KleisliMonadInstance<F, L> {
    override fun FF(): Monad<F> = MF
}

class DaggerKleisliMonadErrorInstance<F, L> @Inject constructor(val MF: MonadError<F, L>) : KleisliMonadErrorInstance<F, L, L> {
    override fun FF(): MonadError<F, L> = MF
}