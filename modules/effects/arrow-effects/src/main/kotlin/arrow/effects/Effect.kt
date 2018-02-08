package arrow.effects

import arrow.Kind
import arrow.TC
import arrow.core.Either
import arrow.typeclass

@typeclass
interface Effect<F> : Async<F>, TC {
    fun <A> runAsync(fa: Kind<F, A>, cb: (Either<Throwable, A>) -> Kind<F, Unit>): Kind<F, Unit>
}
