package arrow.dagger.effects.instances

import arrow.effects.typeclasses.*
import javax.inject.Inject

class DaggerMonadSuspendSyntaxInstance<F> @Inject constructor(val monadSuspend: MonadSuspend<F>) : MonadSuspendSyntax<F> {
    override fun monadSuspend(): MonadSuspend<F> = monadSuspend
}

class DaggerAsyncSyntaxInstance<F> @Inject constructor(val async: Async<F>) : AsyncSyntax<F> {
    override fun async(): Async<F> = async
}

class DaggerEffectSyntaxInstance<F> @Inject constructor(val effect: Effect<F>) : EffectSyntax<F> {
    override fun effect(): Effect<F> = effect
}