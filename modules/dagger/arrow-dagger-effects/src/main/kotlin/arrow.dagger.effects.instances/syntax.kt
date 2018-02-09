package arrow.dagger.effects.instances

import arrow.effects.*
import javax.inject.Inject

class DaggerMonadSuspendSyntaxInstance<F> @Inject constructor(val monadSuspend: MonadSuspend<F>) : MonadSuspendSyntax<F> {
    override fun monadSuspend(): MonadSuspend<F> = monadSuspend
}