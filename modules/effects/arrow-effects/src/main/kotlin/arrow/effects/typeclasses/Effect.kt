package arrow.effects.typeclasses

import arrow.Kind
import arrow.core.Either

interface Effect<F> : Async<F> {
    fun <A> runAsync(fa: Kind<F, A>, cb: (Either<Throwable, A>) -> Kind<F, Unit>): Kind<F, Unit>
}
