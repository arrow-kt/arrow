package arrow.dagger.effects.instances

import arrow.effects.*
import arrow.typeclasses.*
import dagger.Module
import dagger.Provides
import javax.inject.Inject

@Module
class IOInstances {

    @Provides
    fun ioFunctor(): Functor<IOHK> = IO.functor()

    @Provides
    fun ioApplicative(): Applicative<IOHK> = IO.applicative()

    @Provides
    fun ioApplicativeError(): ApplicativeError<IOHK, Throwable> = IO.applicativeError()

    @Provides
    fun ioMonad(): Monad<IOHK> = IO.monad()

    @Provides
    fun ioMonadError(): MonadError<IOHK, Throwable> = IO.monadError()

    @Provides
    fun ioMonadSuspend(): MonadSuspend<IOHK> = IO.monadSuspend()

    @Provides
    fun ioAsync(): Async<IOHK> = IO.async()

    @Provides
    fun ioEffect(): Effect<IOHK> = IO.effect()

}

class DaggerIOSemigroupInstance<A> @Inject constructor(val monoidA: Monoid<A>) : IOSemigroupInstance<A> {
    override fun SG(): Semigroup<A> = monoidA
}

class DaggerIOMonoidInstance<A> @Inject constructor(val monoidA: Monoid<A>) : IOMonoidInstance<A> {
    override fun SM(): Monoid<A> = monoidA
}