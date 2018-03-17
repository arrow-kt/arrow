package arrow.dagger.effects.instances

import arrow.effects.*
import javax.inject.Inject

class DaggerMonadSuspendSyntaxInstance<F, E> @Inject constructor(val monadSuspend: MonadSuspend<F, E>) : MonadSuspendSyntax<F, E> {
    override fun monadSuspend(): MonadSuspend<F, E> = monadSuspend
}

class DaggerAsyncSyntaxInstance<F, E> @Inject constructor(val async: Async<F, E>) : AsyncSyntax<F, E> {
    override fun async(): Async<F, E> = async
}

class DaggerEffectSyntaxInstance<F, E> @Inject constructor(val effect: Effect<F, E>) : EffectSyntax<F, E> {
    override fun effect(): Effect<F, E> = effect
}
