package arrow.dagger.instances

import arrow.data.StateTKindPartial
import arrow.instances.StateTApplicativeInstance
import arrow.instances.StateTFunctorInstance
import arrow.instances.StateTMonadErrorInstance
import arrow.instances.StateTMonadInstance
import arrow.typeclasses.*
import dagger.Module
import dagger.Provides
import javax.inject.Inject

@Module
abstract class StateTInstances<F, S> {

    @Provides
    fun stateTFunctor(ev: DaggerStateTFunctorInstance<F, S>): Functor<StateTKindPartial<F, S>> = ev

    @Provides
    fun stateTApplicative(ev: DaggerStateTApplicativeInstance<F, S>): Applicative<StateTKindPartial<F, S>> = ev

    @Provides
    fun stateTMonad(ev: DaggerStateTMonadInstance<F, S>): Monad<StateTKindPartial<F, S>> = ev

    @Provides
    fun stateTApplicativeError(ev: DaggerStateTMonadErrorInstance<F, S>): ApplicativeError<StateTKindPartial<F, S>, S> = ev

    @Provides
    fun stateTMonadError(ev: DaggerStateTMonadErrorInstance<F, S>): MonadError<StateTKindPartial<F, S>, S> = ev
}

class DaggerStateTFunctorInstance<F, L> @Inject constructor(val FF: Functor<F>) : StateTFunctorInstance<F, L> {
    override fun FF(): Functor<F> = FF
}

class DaggerStateTApplicativeInstance<F, L> @Inject constructor(val MF: Monad<F>) : StateTApplicativeInstance<F, L> {
    override fun FF(): Monad<F> = MF
}

class DaggerStateTMonadInstance<F, L> @Inject constructor(val MF: Monad<F>) : StateTMonadInstance<F, L> {
    override fun FF(): Monad<F> = MF
}

class DaggerStateTMonadErrorInstance<F, L> @Inject constructor(val MF: MonadError<F, L>) : StateTMonadErrorInstance<F, L, L> {
    override fun FF(): MonadError<F, L> = MF
}