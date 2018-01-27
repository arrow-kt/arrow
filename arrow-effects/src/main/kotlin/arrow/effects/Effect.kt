package arrow.effects

import arrow.HK
import arrow.TC
import arrow.core.Either
import arrow.typeclass

@typeclass
interface Effect<F> : Async<F>, TC {
    fun <A> runAsync(fa: HK<F, A>, cb: (Either<Throwable, A>) -> HK<F, Unit>): HK<F, Unit>
}
